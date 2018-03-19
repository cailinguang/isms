package com.vw.isms.web.request;

public class UpdateArchiveStatusRequest {
    private boolean archived;

    public boolean isArchived() {
        return this.archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
