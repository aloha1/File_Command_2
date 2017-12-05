package com.bgsltd.file_command.utils.files;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.bgsltd.file_command.R;
import com.bgsltd.file_command.activities.MainActivity;
import com.bgsltd.file_command.database.CryptHandler;
import com.bgsltd.file_command.database.models.EncryptedEntry;
import com.bgsltd.file_command.exceptions.CryptException;
import com.bgsltd.file_command.filesystem.BaseFile;
import com.bgsltd.file_command.fragments.MainFragment;
import com.bgsltd.file_command.fragments.preference_fragments.Preffrag;
import com.bgsltd.file_command.services.EncryptService;
import com.bgsltd.file_command.ui.dialogs.GeneralDialogCreation;
import com.bgsltd.file_command.utils.OpenMode;
import com.bgsltd.file_command.utils.ServiceWatcherUtil;
import com.bgsltd.file_command.utils.provider.UtilitiesProviderInterface;

/**
 * Provides useful interfaces and methods for encryption/decryption
 *
 * @author Emmanuel
 *         on 25/5/2017, at 16:55.
 */

public class EncryptDecryptUtils {

    public static final String DECRYPT_BROADCAST = "decrypt_broadcast";
    /**
     * Queries database to map path and password.
     * Starts the encryption process after database query
     *
     * @param path     the path of file to encrypt
     * @param password the password in plaintext
     */
    public static void startEncryption(Context c, final String path, final String password,
                                       Intent intent) throws Exception {
        CryptHandler cryptHandler = new CryptHandler(c);
        EncryptedEntry encryptedEntry = new EncryptedEntry(path.concat(CryptUtil.CRYPT_EXTENSION),
                password);
        cryptHandler.addEntry(encryptedEntry);

        // start the encryption process
        ServiceWatcherUtil.runService(c, intent);
    }


    public static void decryptFile(Context c, final MainActivity mainActivity, final MainFragment main, OpenMode openMode,
                                   BaseFile sourceFile, String decryptPath,
                                   UtilitiesProviderInterface utilsProvider,
                                   boolean broadcastResult) {

        Intent decryptIntent = new Intent(main.getContext(), EncryptService.class);
        decryptIntent.putExtra(EncryptService.TAG_OPEN_MODE, openMode.ordinal());
        decryptIntent.putExtra(EncryptService.TAG_CRYPT_MODE,
                EncryptService.CryptEnum.DECRYPT.ordinal());
        decryptIntent.putExtra(EncryptService.TAG_SOURCE, sourceFile);
        decryptIntent.putExtra(EncryptService.TAG_DECRYPT_PATH, decryptPath);
        decryptIntent.putExtra(EncryptService.TAG_BROADCAST_RESULT, broadcastResult);
        SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(main.getContext());

        EncryptedEntry encryptedEntry = null;

        try {
            encryptedEntry = findEncryptedEntry(main.getContext(), sourceFile.getPath());
        } catch (CryptException e) {
            e.printStackTrace();

            // we couldn't find any entry in database or lost the key to decipher
            Toast.makeText(main.getContext(), main.getActivity().getResources().getString(R.string.crypt_decryption_fail), Toast.LENGTH_LONG).show();
            return;
        }

        DecryptButtonCallbackInterface decryptButtonCallbackInterface =
                new DecryptButtonCallbackInterface() {
                    @Override
                    public void confirm(Intent intent) {
                        ServiceWatcherUtil.runService(main.getContext(), intent);
                    }

                    @Override
                    public void failed() {
                        Toast.makeText(main.getContext(), main.getActivity().getResources().getString(R.string.crypt_decryption_fail_password), Toast.LENGTH_LONG).show();
                    }
                };

        switch (encryptedEntry.getPassword()) {
            case Preffrag.ENCRYPT_PASSWORD_FINGERPRINT:
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        GeneralDialogCreation.showDecryptFingerprintDialog(c,
                                mainActivity, decryptIntent, utilsProvider.getAppTheme(), decryptButtonCallbackInterface);
                    } else throw new CryptException();
                } catch (CryptException e) {
                    e.printStackTrace();

                    Toast.makeText(main.getContext(),
                            main.getResources().getString(R.string.crypt_decryption_fail),
                            Toast.LENGTH_LONG).show();
                }
                break;
            case Preffrag.ENCRYPT_PASSWORD_MASTER:
                try {
                    GeneralDialogCreation.showDecryptDialog(c,
                            mainActivity, decryptIntent, utilsProvider.getAppTheme(),
                            CryptUtil.decryptPassword(c, preferences1.getString(Preffrag.PREFERENCE_CRYPT_MASTER_PASSWORD,
                                    Preffrag.PREFERENCE_CRYPT_MASTER_PASSWORD_DEFAULT)), decryptButtonCallbackInterface);
                } catch (CryptException e) {
                    e.printStackTrace();


                    Toast.makeText(main.getContext(),
                            main.getResources().getString(R.string.crypt_decryption_fail),
                            Toast.LENGTH_LONG).show();
                }
                break;
            default:
                GeneralDialogCreation.showDecryptDialog(c, mainActivity, decryptIntent,
                        utilsProvider.getAppTheme(), encryptedEntry.getPassword(),
                        decryptButtonCallbackInterface);
                break;
        }
    }

    /**
     * Queries database to find entry for the specific path
     *
     * @param path the path to match with
     * @return the entry
     */
    private static EncryptedEntry findEncryptedEntry(Context context, String path) throws CryptException {

        CryptHandler handler = new CryptHandler(context);

        EncryptedEntry matchedEntry = null;
        // find closest path which matches with database entry
        for (EncryptedEntry encryptedEntry : handler.getAllEntries()) {
            if (path.contains(encryptedEntry.getPath())) {

                if (matchedEntry == null || matchedEntry.getPath().length() < encryptedEntry.getPath().length()) {
                    matchedEntry = encryptedEntry;
                }
            }
        }
        return matchedEntry;
    }

    public interface EncryptButtonCallbackInterface {

        /**
         * Callback fired when we've just gone through warning dialog before encryption
         *
         * @param intent
         * @throws Exception
         */
        void onButtonPressed(Intent intent) throws Exception;

        /**
         * Callback fired when user has entered a password for encryption
         * Not called when we've a master password set or enable fingerprint authentication
         *
         * @param intent
         * @param password the password entered by user
         * @throws Exception
         */
        void onButtonPressed(Intent intent, String password) throws Exception;
    }

    public interface DecryptButtonCallbackInterface {
        /**
         * Callback fired when we've confirmed the password matches the database
         *
         * @param intent
         */
        void confirm(Intent intent);

        /**
         * Callback fired when password doesn't match the value entered by user
         */
        void failed();
    }

}