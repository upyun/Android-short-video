package org.lasque.tusdkvideodemo.editor.component.helper;

import java.util.LinkedList;

/**
 * @author MirsFang
 * <p>
 * <p>
 * 用来管理Text贴纸和StickerImage贴纸的顺序问题
 * <p>
 * org.lasque.tusdkvideodemo.editor.component.helper
 * @date 2019-04-24 15:30
 */
public class EditorTextAndStickerRankHelper {
    /** 上次应用的特效数据 **/
    private LinkedList mMemeoBackupEntityList = new LinkedList();
    /** 备份实体类列表 **/
    private LinkedList mBackupEntityList = new LinkedList();


    public LinkedList getMemeoBackupLinkedList() {
        return mMemeoBackupEntityList;
    }

    public LinkedList getBackupLinkedList() {
        return mBackupEntityList;
    }




}
