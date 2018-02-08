// ProgressListener.aidl
package com.bgsltd.filecommande;

// Declare any non-default types here with import statements
import com.bgsltd.filecommande.utils.DataPackage;
interface ProgressListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
        void onUpdate(in DataPackage dataPackage);
        void refresh();
}
