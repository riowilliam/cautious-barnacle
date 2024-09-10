package com.fision.serviceImpl;

import com.fision.dto.ContractListDto;
import com.fision.dto.ContractPagingListDto;
import com.fision.dto.ContractRequestDto;
import com.fision.dto.ItemDetailsListDto;
import com.fision.entity.TbContract;
import com.fision.entity.TbItemDetails;
import com.fision.repository.TbContractRepository;
import com.fision.repository.TbItemDetailsRepository;
import com.fision.repository.TxPaidItemRepository;
import com.fision.service.ContractService;
import com.fision.utils.DateTimeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class ContractServiceImpl implements ContractService {
    @Value("${contract.code.prefix}")
    String contractCodePrefix;

    @Autowired
    TbContractRepository tbContractRepository;

    @Autowired
    TbItemDetailsRepository tbItemDetailsRepository;

    @Autowired
    TxPaidItemRepository txPaidItemRepository;

    @Override
    public Page<ContractPagingListDto> getContractListPaging(int pageNo, int pageSize, String sortBy, String sortOrder, String contractName, Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(pageNo, pageSize,
                sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        return tbContractRepository.getContractListPaging(contractName, startDate, endDate, pageable);
    }

    @Override
    @Transactional
    public void saveContract(String username, ContractRequestDto contractRequest) {
        /* Save Contract */
        TbContract tbContract = new TbContract();
        tbContract.setContractName(contractRequest.getContractName());
        tbContract.setContractCode(generateContractCode());
        tbContract.setCreatedBy(username);
        tbContract.setModifiedBy(username);
        tbContract.setRevision(contractRequest.getRevision());
        tbContractRepository.save(tbContract);

        /* Save Item Details */
        List<TbItemDetails> tbItemDetailsList = new LinkedList<>();
        for(ItemDetailsListDto itemDetail : contractRequest.getItemDetailList()) {
            TbItemDetails tbItemDetails = new TbItemDetails();
            tbItemDetails.setItemName(itemDetail.getItemName());
            tbItemDetails.setContractCode(generateContractCode());
            tbItemDetails.setTotalQuantity(itemDetail.getTotalQuantity());
            tbItemDetails.setRemainingQuantity(itemDetail.getTotalQuantity());
            tbItemDetails.setRevision(contractRequest.getRevision());
            tbItemDetails.setCreatedBy(username);
            tbItemDetails.setModifiedBy(username);
            tbItemDetailsList.add(tbItemDetails);
        }
        tbItemDetailsRepository.saveAll(tbItemDetailsList);
    }

    @Override
    public void updateContract(String username, TbContract tbContract, ContractRequestDto contractRequest) {
        /* Save Contract */
        TbContract tbContractNew = new TbContract();
        tbContractNew.setContractName(contractRequest.getContractName());
        tbContractNew.setContractCode(tbContract.getContractCode());
        tbContractNew.setCreatedBy(username);
        tbContractNew.setModifiedBy(username);
        tbContractNew.setRevision(contractRequest.getRevision());
        tbContractRepository.save(tbContractNew);

        /* Save Item Details */
        List<TbItemDetails> tbItemDetailsList = new LinkedList<>();
        for(ItemDetailsListDto itemDetail : contractRequest.getItemDetailList()) {
            TbItemDetails tbItemDetails = new TbItemDetails();
            tbItemDetails.setItemName(itemDetail.getItemName());
            tbItemDetails.setContractCode(tbContract.getContractCode());
            tbItemDetails.setTotalQuantity(itemDetail.getTotalQuantity());
            tbItemDetails.setRemainingQuantity(itemDetail.getTotalQuantity());
            tbItemDetails.setRevision(contractRequest.getRevision());
            tbItemDetails.setCreatedBy(username);
            tbItemDetails.setModifiedBy(username);
            tbItemDetailsList.add(tbItemDetails);
        }
        tbItemDetailsRepository.saveAll(tbItemDetailsList);
    }

    @Override
    public TbContract getContractByCode(String contractCode) {
        return tbContractRepository.findByContractCode(contractCode);
    }

    @Override
    public Boolean checkRemainingQuantity(List<ItemDetailsListDto> itemDetailsListDto, TbContract tbContract) {
        Boolean isAvailable = null;
        Integer paidQuantity = 0;
        for (ItemDetailsListDto detailList : itemDetailsListDto) {
            paidQuantity = txPaidItemRepository.getPaidQuantity(tbContract.getContractCode(), detailList.getItemName());
            isAvailable = detailList.getTotalQuantity() >= paidQuantity ? Boolean.TRUE : Boolean.FALSE;
            if(!isAvailable) break;
        }
        return isAvailable;
    }

    @Override
    public List<ContractListDto> getContractList(String contractName) {
        List<Object[]> results = tbContractRepository.findContractWithHighestRevision(contractName);

        Map<String, List<ItemDetailsListDto>> contractItemMap = new HashMap<>();

        for (Object[] result : results) {
            String contractCode = (String) result[0];
            String itemName = (String) result[1];
            Integer totalQuantity = (Integer) result[2];
            Integer remainingQuantity = (Integer) result[3];
            Integer paidQuantity = (Integer) result[4];

            ItemDetailsListDto itemDetails = new ItemDetailsListDto();
            itemDetails.setItemName(itemName);
            itemDetails.setTotalQuantity(totalQuantity);
            itemDetails.setRemainingQuantity(remainingQuantity);
            itemDetails.setPaidQuantity(paidQuantity);

            contractItemMap.computeIfAbsent(contractCode, k -> new ArrayList<>()).add(itemDetails);
        }

        List<ContractListDto> contractList = new ArrayList<>();
        for (Map.Entry<String, List<ItemDetailsListDto>> entry : contractItemMap.entrySet()) {
            contractList.add(new ContractListDto(entry.getKey(), entry.getValue()));
        }

        return contractList;
    }

    private String generateContractCode(){
        return contractCodePrefix + DateTimeHelper.nowToString();
    }
}
