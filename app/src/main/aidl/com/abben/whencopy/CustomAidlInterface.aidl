// CustomAidlInterface.aidl
package com.abben.whencopy;

// Declare any non-default types here with import statements

interface CustomAidlInterface {


    /**初始化*/
    void initServiceVisibilityFlag(in boolean visibilitySearch, in boolean visibilityTranslation,
               in boolean visibilityInsertevents);

    /**改变视图*/
    void changeView(in int changeVisibityIndex , in boolean visibility);
}
