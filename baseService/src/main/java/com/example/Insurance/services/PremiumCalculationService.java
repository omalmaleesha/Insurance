package com.example.Insurance.services;


import com.example.Insurance.entities.qty.AbstractRisk;
import com.example.Insurance.entities.qty.Quotation;

public interface PremiumCalculationService {

    void calculateQuotation(Quotation quotation, AbstractRisk risk);

}