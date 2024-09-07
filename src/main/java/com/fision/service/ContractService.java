package com.fision.service;

import com.fision.dto.ContractListDto;
import com.fision.dto.ContractRequestDto;
import com.fision.dto.ItemDetailsListDto;
import com.fision.entity.TbContract;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ContractService {
    Page<ContractListDto> getContractListPaging(int pageNo, int pageSize, String sortBy, String sortOrder,
                                                String contractName, Date startDate, Date endDate);
    void saveContract(String username, ContractRequestDto contractRequest);
    void updateContract(String username, TbContract tbContract, ContractRequestDto contractRequest);

    TbContract getContractByCode(String contractCode);
    Boolean checkRemainingQuantity(List<ItemDetailsListDto> itemDetailsListDto, TbContract tbContract);
    List<Map<String, Object>> getContractList(String contractName);
}
