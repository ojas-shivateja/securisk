package com.insure.rfq.generator;

import com.insure.rfq.entity.EmployeeDepedentDetailsEntity;
import com.insure.rfq.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AgeBindingData {
    @Autowired
    private EmployeeRepository employeeRepository;

    /*
     * This method returns 1+5, Parents only from total data for finding 1+3 ID's
     */
    public Set<String> getAllSetOfRelationOfOnePlusFive(List<EmployeeDepedentDetailsEntity> globalListDetails) {
        return globalListDetails.stream()
                .filter(data -> data.getRelationship().trim().matches("(?i)Father?.+")
                        || data.getRelationship().trim().matches("(?i)Mother?.+")
                        || data.getRelationship().trim().matches("(?i)Par?.+"))
                .map(data -> {
                    String employeeId = data.getEmployeeId();
                    return employeeId;
                }).collect(Collectors.toSet());

    }

    /*
     * This methods returns only 1+3 ID's
     */
    public Set<String> getAllSetOfRelationOfOnePlusThree(List<EmployeeDepedentDetailsEntity> globalListDetails) {
        Set<String> allSetOfRelation = getAllSetOfRelationOfOnePlusFive(globalListDetails);
        Set<String> collect = globalListDetails.stream().map(EmployeeDepedentDetailsEntity::getEmployeeId)
                .collect(Collectors.toSet());

        for (String data : allSetOfRelation) {
            collect.remove(data);
        }
        log.info(" 1+3 - {} and size is {}", collect, collect.size());
        return collect;

    }

    /*
     * This methods globally calculates the count of ages for 1+3, 1+5 , Parents
     */
    public double getCount(String relation, double minAge, double maxAge, String employeeId, double suminsured,
                           List<EmployeeDepedentDetailsEntity> globalListDetails) {

        return globalListDetails.stream().filter(data -> data.getEmployeeId().equalsIgnoreCase(employeeId))
                .filter(data -> data.getRelationship().trim().matches("(?i)" + relation + "?.+"))
                .filter(data -> data.getSumInsured() == suminsured)
                .filter(data -> Double.parseDouble(data.getAge()) >= minAge)
                .filter(data -> Double.parseDouble(data.getAge()) <= maxAge).count();

    }


    /*
     * This method returns the count of ages for 1+5 with parents
     */
    public int getCountOfData(String rfqid, String relation, double minAge, double maxAge, double suminsured,
                              List<EmployeeDepedentDetailsEntity> globalData) {
        int count = 0;
        for (String employeeId : getAllSetOfRelationOfOnePlusFive(globalData)) {
            count += getCount(relation, minAge, maxAge, employeeId, suminsured, globalData);
        }

        return count;
    }

    public int getCountOfDataOfOnePlusThree(String rfqid, String relation, double minAge, double maxAge,
                                            double suminsured, List<EmployeeDepedentDetailsEntity> globalData) {
        int count = 0;
        Set<String> data = getAllSetOfRelationOfOnePlusThree(globalData);
        List<String> data1 = new ArrayList<>();
        for (String employeeId : data) {
            data1.add(employeeId);

            count += getCount(relation, minAge, maxAge, employeeId, suminsured, globalData);
        }
        log.info(relation + " value :{} ", data);
        return count;
    }


    /*
     * This methods filters only combined 1+5 with parents only ID's
     */
    public Set<String> onePlusFive(List<EmployeeDepedentDetailsEntity> globalData) {
        Set<String> finaldataOnePlusFive = new HashSet<>();
        Set<String> finaldata = getAllSetOfRelationOfOnePlusFive(globalData);
        log.info(" finaL DATA { }", finaldata);

        // working here
        for (String employeeId : finaldata) {

            finaldataOnePlusFive
                    .addAll(globalData.stream().filter(data -> data.getEmployeeId().equalsIgnoreCase(employeeId))
                            .filter(data -> data.getRelationship().matches("(?i)Spous?.+")
                                    || data.getRelationship().trim().matches("(?i)Chil?.+")
                                    || data.getRelationship().trim().matches("(?i)husba?.+")
                                    || data.getRelationship().trim().matches("(?i)Wif?.+")
                                    || data.getRelationship().trim().matches("(?i)Son?.+")
                                    || data.getRelationship().trim().matches("(?i)Daug?.+")
                                    || data.getRelationship().trim().matches("(?i)Father?.+")
                                    || data.getRelationship().trim().matches("(?i)Mother?.+")
                                    || data.getRelationship().trim().matches("(?i)Par?.+"))
                            .map(EmployeeDepedentDetailsEntity::getEmployeeId).collect(Collectors.toSet()));
        }
        log.info(" 1+5 data :-{} and size is {}", finaldataOnePlusFive, finaldataOnePlusFive.size());
        return finaldataOnePlusFive;

    }

    public int getCountOfDataOfOnePlusFive(String rfqid, String relation, double minAge, double maxAge,
                                           double suminsured, List<EmployeeDepedentDetailsEntity> globalData) {

        int count = 0;
        for (String employeeId : onePlusFive(globalData)) {

            count += getCount(relation, minAge, maxAge, employeeId, suminsured, globalData);
        }

        return count;
    }

    /*
     * Logic for Parents only
     */


    public Set<String> getAllSetOfRelationOfParentsOnly(List<EmployeeDepedentDetailsEntity> globalData) {


        Set<String> finaldata = getAllSetOfRelationOfOnePlusFive(globalData);
        Set<String> oneThree = getAllSetOfRelationOfOnePlusThree(globalData);
        Set<String> oneFive = onePlusFive(globalData);

        for (String ok : oneThree) {
            finaldata.remove(ok);
        }

        for (String five : oneFive) {
            finaldata.remove(five);
        }
        log.info(" parents only data :-{} and size is {}", finaldata, finaldata.size());
        return finaldata;
    }

}
