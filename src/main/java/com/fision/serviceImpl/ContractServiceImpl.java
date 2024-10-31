package com.fision.serviceImpl;

import com.fision.dto.*;
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
import java.util.stream.Collectors;

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
    public TbContract getContractByCodeAndRevision(String contractCode, Integer revision) {
        return tbContractRepository.findByContractCodeAndRevision(contractCode, revision);
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
    public Boolean checkExistingItemDetails(List<ItemDetailsListDto> itemDetailsListDto, TbContract tbContract) {
        List<TbItemDetails> existingTbItemDetailsList = tbItemDetailsRepository.findByContractCodeAndRevision(tbContract.getContractCode(), tbContract.getRevision());

        // Convert itemDetailsListDto to a set of item names for easy lookup
        Set<String> dtoItemNames = itemDetailsListDto.stream()
                .map(ItemDetailsListDto::getItemName)
                .collect(Collectors.toSet());

        // If sizes differ, determine items to delete and set isDiff to true
        if (itemDetailsListDto.size() < existingTbItemDetailsList.size()) {
            List<TbItemDetails> itemsToDelete = existingTbItemDetailsList.stream()
                    .filter(item -> !dtoItemNames.contains(item.getItemName()))
                    .collect(Collectors.toList());

            // Delete items not present in itemDetailsListDto
            tbItemDetailsRepository.deleteAll(itemsToDelete);
            return Boolean.TRUE;
        }

        // If sizes are the same, compare quantities
        Map<String, Integer> existingItemQuantities = existingTbItemDetailsList.stream()
                .collect(Collectors.toMap(TbItemDetails::getItemName, TbItemDetails::getTotalQuantity));

        for (ItemDetailsListDto dtoItem : itemDetailsListDto) {
            Integer existingQuantity = existingItemQuantities.get(dtoItem.getItemName());

            // If any item’s quantity doesn’t match, return true (indicating a difference)
            if (existingQuantity == null || !existingQuantity.equals(dtoItem.getTotalQuantity())) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE; // All items match in size and quantity, no deletions needed
    }

    @Override
    public List<ContractListDto> getContractList(String contractNameParam, String contractCodeParam) {
        List<Object[]> results = tbContractRepository.findContractWithHighestRevision(contractNameParam, contractCodeParam);

        // Map to store item details by contract code
        Map<String, ContractListDto> contractMap = new HashMap<>();

        for (Object[] result : results) {
            String contractCode = (String) result[0];
            String contractName = (String) result[1];
            String itemName = (String) result[2];
            Integer totalQuantity = (Integer) result[3];
            Integer remainingQuantity = (Integer) result[4];
            Integer paidQuantity = (Integer) result[5];

            // Create ItemDetailsListDto for each item
            ItemDetailsListDto itemDetails = new ItemDetailsListDto();
            itemDetails.setItemName(itemName);
            itemDetails.setTotalQuantity(totalQuantity);
            itemDetails.setRemainingQuantity(remainingQuantity);
            itemDetails.setPaidQuantity(paidQuantity);

            // If the contract is already in the map, retrieve it, otherwise create a new entry
            ContractListDto contractListDto = contractMap.get(contractCode);
            if (contractListDto == null) {
                // If not in the map, create a new ContractListDto
                contractListDto = new ContractListDto(contractCode, contractName, new ArrayList<>());
                contractMap.put(contractCode, contractListDto);
            }

            // Add item details to the contract's itemList
            contractListDto.getItemList().add(itemDetails);
        }

        // Convert the map values to a list of ContractListDto and return
        return new ArrayList<>(contractMap.values());
    }

    @Override
    public List<ContractRevisionListDto> getContractRevisionList(String contractCodeParam) {
        List<Object[]> results = tbContractRepository.findContractRevisionList(contractCodeParam);

        Map<Integer, ContractRevisionListDto> revisionMap = new HashMap<>();

        for (Object[] result : results) {
            Integer revision = (Integer) result[0];
            String createdBy = (String) result[1];
            Date createdDate = (Date) result[2];
            String itemName = (String) result[3];
            Integer totalQuantity = (Integer) result[4];
            Integer remainingQuantity = (Integer) result[5];
            Integer paidQuantity = (Integer) result[6];

            // Create ItemDetailsListDto for each item
            ItemDetailsListDto itemDetails = new ItemDetailsListDto();
            itemDetails.setItemName(itemName);
            itemDetails.setTotalQuantity(totalQuantity);
            itemDetails.setRemainingQuantity(remainingQuantity);
            itemDetails.setPaidQuantity(paidQuantity);

            // If the revision already exists, retrieve it, otherwise create a new entry
            ContractRevisionListDto revisionListDto = revisionMap.get(revision);
            if (revisionListDto == null) {
                // If not in the map, create a new ContractRevisionListDto
                revisionListDto = new ContractRevisionListDto(revision, createdBy, createdDate, new ArrayList<>());
                revisionMap.put(revision, revisionListDto);
            }

            // Add item details to the revision's itemList
            revisionListDto.getItemList().add(itemDetails);
        }

        // Convert the map values to a list of ContractRevisionListDto and return
        return new ArrayList<>(revisionMap.values());
    }

    private String generateContractCode(){
        return contractCodePrefix + DateTimeHelper.nowToString();
    }
}
