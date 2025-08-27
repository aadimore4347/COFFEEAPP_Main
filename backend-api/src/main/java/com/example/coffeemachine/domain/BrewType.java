package com.example.coffeemachine.domain;

/**
 * Enumeration representing different types of coffee beverages
 * that can be brewed by the coffee machines.
 * 
 * Used for tracking usage patterns and consumption analytics.
 */
public enum BrewType {
    /**
     * Single shot espresso (typically 30ml)
     */
    ESPRESSO,
    
    /**
     * Double shot espresso (typically 60ml)
     */
    DOUBLE_ESPRESSO,
    
    /**
     * Americano - espresso with hot water (typically 250ml)
     */
    AMERICANO,
    
    /**
     * Cappuccino - espresso with steamed milk and foam (typically 150ml)
     */
    CAPPUCCINO,
    
    /**
     * Latte - espresso with steamed milk (typically 200ml)
     */
    LATTE,
    
    /**
     * Macchiato - espresso with a dollop of steamed milk (typically 40ml)
     */
    MACCHIATO,
    
    /**
     * Mocha - espresso with chocolate and steamed milk (typically 200ml)
     */
    MOCHA,
    
    /**
     * Flat White - espresso with steamed milk (typically 160ml)
     */
    FLAT_WHITE
}