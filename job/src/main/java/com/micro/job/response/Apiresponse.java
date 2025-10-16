package com.micro.job.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Apiresponse <T>{
    public  String status;
    public  String message;
    public T data;


}
