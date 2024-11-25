package com.fision.service;

import com.fision.dto.*;
import com.fision.entity.primary.TbContract;
import com.fision.entity.primary.TbPartner;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface ContractService {
    Page<ContractPagingListDto> getContractListPaging(int pageNo, int pageSize, String sortBy, String sortOrder,
                                                      String contractName, String partnerName, Date startDate, Date endDate);
    void saveContract(String username, ContractRequestDto contractRequest, TbPartner tbPartner);
    void updateContract(String username, TbContract tbContract, ContractRequestDto contractRequest, TbPartner tbPartner);

    TbContract getContractByNoWithLatestRevision(String contractNo);

    TbContract getContractByNoAndRevision(String contractNo, Integer revision);
    Boolean checkRemainingQuantity(List<ItemDetailsListDto> itemDetailsListDto, TbContract tbContract);
    Boolean  checkExistingItemDetails(List<ItemDetailsListDto> itemDetailsListDto, TbContract tbContract);
    List<ContractListDto> getContractList(String contractName, String contractNo);
    List<ContractRevisionListDto> getContractRevisionList(String contractNo);

}
