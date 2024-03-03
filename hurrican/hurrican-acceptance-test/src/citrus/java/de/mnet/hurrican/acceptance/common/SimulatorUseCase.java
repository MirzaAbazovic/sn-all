/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.14
 */
package de.mnet.hurrican.acceptance.common;

/**
 * List of available FFM Simulator use cases. Just in case we need to use the FFM Simulator in an E2E test environment
 * or in local test environment.
 *
 *
 */
public enum SimulatorUseCase {

    FFM_Default,
    FFM_CreateOrder_01,
    FFM_CreateOrder_02,
    FFM_CreateOrder_03,
    FFM_CreateOrder_04,
    FFM_CreateOrder_05,
    FFM_CreateOrder_06,
    FFM_CreateOrder_07,
    FFM_CreateOrder_08,
    FFM_CreateOrder_09,
    FFM_CreateOrder_10,
    FFM_CreateOrder_11,
    FFM_CreateOrder_12,
    FFM_UpdateOrder_01,
    FFM_UpdateOrder_02,
    FFM_NotifyUpdateOrder_01,
    FFM_NotifyUpdateOrder_02,
    FFM_NotifyUpdateOrder_03,
    FFM_NotifyUpdateOrder_04,
    FFM_NotifyUpdateOrder_05,
    FFM_NotifyFeedbackOrder_01,
    FFM_NotifyFeedbackOrder_02,
    FFM_NotifyFeedbackOrder_03,
    FFM_DeleteOrder_01,
    FFM_ErrorService_01,
    FFM_ErrorService_02,
    FFM_ErrorService_03,

    WorkforceData_01,

    CommandCustomerData_01,

    CommandLocationData_01,

    CommandStiftData_01,
    CommandStiftData_02,

    Hurrican_CPS_Endpoint_01,
    Hurrican_CPS_Endpoint_02,

    SwitchMigration_MigrateOrders_01,
    SwitchMigration_MigrateOrders_02,
    SwitchMigration_MigrateOrders_03,

    ResourceInventory_UpdateOntResource_01,
    ResourceInventory_UpdateOntResource_02,
    ResourceInventory_UpdateOntResource_03,

    ResourceInventory_UpdateDpoResource_01,
    ResourceInventory_UpdateDpoResource_02,
    ResourceInventory_UpdateDpoResource_03,
    ResourceInventory_UpdateDpoResource_04,
    ResourceInventory_UpdateDpoResource_05,
    ResourceInventory_UpdateDpoResource_06,

    ResourceCharacteristics_Update_01,

    PortierungService_01,

    DocumentArchive_01,
    DocumentArchive_02,
    DocumentArchive_03,
    DocumentArchive_04,

    TV_Feed_GeoIds_01,
    TV_Feed_GeoIds_02,
    TV_Feed_GeoIds_03,
    TV_Feed_Data4TechLocations_01,
    TV_Feed_Data4TechLocations_02,
    TV_Feed_Data4TechLocations_03,

    TV_Provider_Availability_01,
    TV_Provider_Availability_02,

    GetOrderDetailsCustomer_01,
    GetOrderDetailsCustomer_02,

    GetLineIds_01,

    GetPublicOrderStatus_01,

    GetCustomerLoginDetails_01,
    CustomerDataUsageDetails_01,

    NotifyReplaceLocation_01,
    NotifyReplaceLocation_02,
    NotifyUpdateLocation_01,
    NotifyUpdateLocation_02,

    AvailabilityService_01,
    AvailabilityService_02,

    WholesaleOrder, /* Umfasst UseCases fuer Wholesale PV) */
    Resource_Order_Management /* Umfasst UseCases fuer Port Handling (Reservierung, Freigabe, usw.) */
}
