package com.insure.rfq.payload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataToEmail {
private List<String>to;
private List<String>documentList;
private String rfqId;
}
