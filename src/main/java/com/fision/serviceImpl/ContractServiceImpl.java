package com.fision.serviceImpl;

import com.fision.dto.*;
import com.fision.entity.primary.TbContract;
import com.fision.entity.primary.TbItemDetails;
import com.fision.entity.primary.TbPartner;
import com.fision.repository.primary.TbContractRepository;
import com.fision.repository.primary.TbItemDetailsRepository;
import com.fision.repository.primary.TbPartneRepository;
import com.fision.repository.primary.TxPaidItemRepository;
import com.fision.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    TbContractRepository tbContractRepository;

    @Autowired
    TbItemDetailsRepository tbItemDetailsRepository;

    @Autowired
    TxPaidItemRepository txPaidItemRepository;

    @Autowired
    TbPartneRepository tbPartneRepository;

    @Override
    public Page<ContractPagingListDto> getContractListPaging(int pageNo, int pageSize, String sortBy, String sortOrder, String contractName, String partnerName, Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(pageNo, pageSize,
                sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        return tbContractRepository.getContractListPaging(contractName, partnerName, startDate, endDate, pageable);
    }

    @Override
    @Transactional
    public void saveContract(String username, ContractRequestDto contractRequest, TbPartner tbPartner) {
        /* Save Contract */
        TbContract tbContract = new TbContract();
        tbContract.setContractName(contractRequest.getContractName());
        tbContract.setContractNo(contractRequest.getContractNo());
        tbContract.setPartnerName(contractRequest.getPartnerName());
        tbContract.setContractDate(contractRequest.getContractDate());
        tbContract.setCreatedBy(username);
        tbContract.setModifiedBy(username);
        tbContract.setRevision(contractRequest.getRevision());
        tbContractRepository.save(tbContract);

        /* Save Partner jika ada penambahan active project */
        tbPartner.setActiveProject(contractRequest.getActiveProject());
        tbPartner.setModifiedBy(username);
        tbPartneRepository.save(tbPartner);

        /* Save Item Details */
        List<TbItemDetails> tbItemDetailsList = new LinkedList<>();
        for(ItemDetailsListDto itemDetail : contractRequest.getItemDetailList()) {
            TbItemDetails tbItemDetails = new TbItemDetails();
            tbItemDetails.setItemName(itemDetail.getItemName());
            tbItemDetails.setContractNo(tbContract.getContractNo());
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
    public void updateContract(String username, TbContract tbContract, ContractRequestDto contractRequest, TbPartner tbPartner) {
        /* Save Contract */
        TbContract tbContractNew = new TbContract();
        tbContractNew.setContractName(contractRequest.getContractName());
        tbContractNew.setContractNo(contractRequest.getContractNo());
        tbContractNew.setPartnerName(contractRequest.getPartnerName());
        tbContractNew.setAddendumDate(contractRequest.getAddendumDate());
        tbContractNew.setContractDate(contractRequest.getContractDate());
        tbContractNew.setCreatedBy(username);
        tbContractNew.setModifiedBy(username);
        tbContractNew.setRevision(contractRequest.getRevision());
        tbContractRepository.save(tbContractNew);

        /* Save Partner jika ada penambahan active project */
        tbPartner.setActiveProject(contractRequest.getActiveProject());
        tbPartner.setModifiedBy(username);
        tbPartneRepository.save(tbPartner);

        /* Save Item Details */
        List<TbItemDetails> tbItemDetailsList = new LinkedList<>();
        for(ItemDetailsListDto itemDetail : contractRequest.getItemDetailList()) {
            TbItemDetails existingTbItem = tbItemDetailsRepository.findByContractNoAndRevisionAndItemName(tbContract.getContractNo(), tbContract.getRevision(), itemDetail.getItemName());
            Double existingPaidQuantity = txPaidItemRepository.getPaidQuantity(tbContract.getContractNo(), itemDetail.getItemName());
            TbItemDetails tbItemDetails = new TbItemDetails();
            tbItemDetails.setItemName(itemDetail.getItemName());
            tbItemDetails.setContractNo(tbContract.getContractNo());
            tbItemDetails.setTotalQuantity(itemDetail.getTotalQuantity());
            tbItemDetails.setRemainingQuantity(existingTbItem != null ? itemDetail.getTotalQuantity() - existingPaidQuantity : itemDetail.getTotalQuantity());
            tbItemDetails.setRevision(contractRequest.getRevision());
            tbItemDetails.setCreatedBy(username);
            tbItemDetails.setModifiedBy(username);
            tbItemDetailsList.add(tbItemDetails);
        }
        tbItemDetailsRepository.saveAll(tbItemDetailsList);
    }

    @Override
    public TbContract getContractByNoWithLatestRevision(String contractNo) {
        return tbContractRepository.findBycontractNoAndMaxRevision(contractNo);
    }

    @Override
    public TbContract getContractByNoAndRevision(String contractNo, Integer revision) {
        return tbContractRepository.findBycontractNoAndRevision(contractNo, revision);
    }

    @Override
    public Boolean checkRemainingQuantity(List<ItemDetailsListDto> itemDetailsListDto, TbContract tbContract) {
        Boolean isAvailable = null;
        Double paidQuantity;
        for (ItemDetailsListDto detailList : itemDetailsListDto) {
            paidQuantity = txPaidItemRepository.getPaidQuantity(tbContract.getContractNo(), detailList.getItemName());
            isAvailable = detailList.getTotalQuantity() >= paidQuantity ? Boolean.TRUE : Boolean.FALSE;
            if(!isAvailable) break;
        }
        return isAvailable;
    }

    @Override
    public Boolean checkExistingItemDetails(List<ItemDetailsListDto> itemDetailsListDto, TbContract tbContract) {
        List<TbItemDetails> existingTbItemDetailsList = tbItemDetailsRepository.findByContractNoAndRevision(tbContract.getContractNo(), tbContract.getRevision());

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
        Map<String, Double> existingItemQuantities = existingTbItemDetailsList.stream()
                .collect(Collectors.toMap(TbItemDetails::getItemName, TbItemDetails::getTotalQuantity));

        for (ItemDetailsListDto dtoItem : itemDetailsListDto) {
            Double existingQuantity = existingItemQuantities.get(dtoItem.getItemName());

            // If any item’s quantity doesn’t match, return true (indicating a difference)
            if (existingQuantity == null || !existingQuantity.equals(dtoItem.getTotalQuantity())) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE; // All items match in size and quantity, no deletions needed
    }

    @Override
    public List<ContractListDto> getContractList(String contractNameParam, String contractNoParam) {
        List<Object[]> results = tbContractRepository.findContractWithHighestRevision(contractNameParam, contractNoParam);

        // Map to store item details by contract code
        Map<String, ContractListDto> contractMap = new HashMap<>();

        for (Object[] result : results) {
            String contractNo = (String) result[0];
            String contractName = (String) result[1];
            ItemDetailsListDto itemDetails = getItemDetailsListDto(result);

            // If the contract is already in the map, retrieve it, otherwise create a new entry
            ContractListDto contractListDto = contractMap.computeIfAbsent(contractNo, c -> new ContractListDto(c, contractName, new ArrayList<>()));

            // Add item details to the contract's itemList
            contractListDto.getItemList().add(itemDetails);
        }

        // Convert the map values to a list of ContractListDto and return
        return new ArrayList<>(contractMap.values());
    }

    @Override
    public List<ContractRevisionListDto> getContractRevisionList(String contractNoParam) {
        List<Object[]> results = tbContractRepository.findContractRevisionList(contractNoParam);

        Map<Integer, ContractRevisionListDto> revisionMap = new HashMap<>();

        for (Object[] result : results) {
            Integer revision = (Integer) result[0];
            String createdBy = (String) result[1];
            Date createdDate = (Date) result[2];
            ItemDetailsListDto itemDetails = getItemDetailsListDtoForRevisionList(result);

            // If the revision already exists, retrieve it, otherwise create a new entry
            ContractRevisionListDto revisionListDto = revisionMap.computeIfAbsent(revision, r -> new ContractRevisionListDto(r, createdBy, createdDate, new ArrayList<>()));

            // Add item details to the revision's itemList
            revisionListDto.getItemList().add(itemDetails);
        }

        // Convert the map values to a list of ContractRevisionListDto and return
        return new ArrayList<>(revisionMap.values());
    }

    private static ItemDetailsListDto getItemDetailsListDto(Object[] result) {
        String itemName = (String) result[2];
        Double totalQuantity = (Double) result[3];
        Double remainingQuantity = (Double) result[4];
        Double paidQuantity = (Double) result[5];

        // Create ItemDetailsListDto for each item
        ItemDetailsListDto itemDetails = new ItemDetailsListDto();
        itemDetails.setItemName(itemName);
        itemDetails.setTotalQuantity(totalQuantity);
        itemDetails.setRemainingQuantity(remainingQuantity);
        itemDetails.setPaidQuantity(paidQuantity);
        return itemDetails;
    }

    private static ItemDetailsListDto getItemDetailsListDtoForRevisionList(Object[] result) {
        String itemName = (String) result[3];
        Double totalQuantity = (Double) result[4];
        Double remainingQuantity = (Double) result[5];
        Double paidQuantity = (Double) result[6];

        // Create ItemDetailsListDto for each item
        ItemDetailsListDto itemDetails = new ItemDetailsListDto();
        itemDetails.setItemName(itemName);
        itemDetails.setTotalQuantity(totalQuantity);
        itemDetails.setRemainingQuantity(remainingQuantity);
        itemDetails.setPaidQuantity(paidQuantity);
        return itemDetails;
    }
}
