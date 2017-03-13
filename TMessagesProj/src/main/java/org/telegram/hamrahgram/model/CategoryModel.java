package org.telegram.hamrahgram.model;




import java.io.Serializable;
import java.util.ArrayList;

public class CategoryModel implements Serializable {

    public ArrayList<Long> dialogIds;

    public void setDialogIds(ArrayList<Long> dialogIds) {
        this.dialogIds = dialogIds;
    }

    public ArrayList<Long> getDialogIds() {

        return dialogIds;
    }
}
