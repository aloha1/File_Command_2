// IMyAidlInterface.aidl
package com.bgsltd.filecommande;

import com.bgsltd.filecommande.Loadlistener;
// Declare any non-default types here with import statements
interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void loadlist(String id);
    void loadRoot();
    void goback(String id);
    void create();
    void registerCallback(Loadlistener load);
}
