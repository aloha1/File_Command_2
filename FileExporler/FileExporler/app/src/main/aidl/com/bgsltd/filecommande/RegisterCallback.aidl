// RegisterCallback.aidl
package com.bgsltd.filecommande;

// Declare any non-default types here with import statements
import com.bgsltd.filecommande.ProgressListener;
import com.bgsltd.filecommande.utils.DataPackage;
interface RegisterCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void registerCallBack(in ProgressListener p);
    List<DataPackage> getCurrent();
}
