package com.company.Domain;

import com.company.Utils.STATUS;

public class FriendRequest {

    private Long idUser1;
    private Long idUser2;
    private STATUS status;

    public FriendRequest(Long idUser1, Long idUser2) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.status = STATUS.PENDING;
    }

    public Long getIdUser1()
    {
        return idUser1;
    }

    public Long getIdUser2() {
        return idUser2;
    }

    public STATUS getStatus() {
        return status;
    }

    public void acceptRequest()
    {
        this.status=STATUS.APPROVED;
    }

    public void rejectRequest()
    {
        this.status=STATUS.REJECTED;
    }
}
