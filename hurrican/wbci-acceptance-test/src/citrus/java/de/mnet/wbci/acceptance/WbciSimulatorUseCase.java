package de.mnet.wbci.acceptance;

/**
 * List of available WBCI Simulator use cases. Just in case we need to use the WBCI Simulator in an E2E test environment
 * or in KFT with DTAG.
 *
 *
 */
public enum WbciSimulatorUseCase {

    DEFAULT_TEQ("DEFAULT_TEQ"),
    /**
     * KUEMRN aufnehmender Carrier *
     */
    VaKueMrnAufStandardAblauf_01("VaKueMrnAuf_01"),
    VaKueMrnAuf_02("VaKueMrnAuf_02"),
    VaKueMrnAuf_03("VaKueMrnAuf_03"),
    VaKueMrnAuf_04("VaKueMrnAuf_04"),
    VaKueMrnAuf_05("VaKueMrnAuf_05"),
    VaKueMrnAuf_06("VaKueMrnAuf_06"),
    VaKueMrnAuf_07("VaKueMrnAuf_07"),
    VaKueMrnAuf_08("VaKueMrnAuf_08"),
    VaKueMrnAuf_09("VaKueMrnAuf_09"),
    VaKueMrnAuf_10("VaKueMrnAuf_10"),
    VaKueMrnAuf_11("VaKueMrnAuf_11"),
    VaKueMrnAuf_12("VaKueMrnAuf_12"),
    VaKueMrnAuf_13("VaKueMrnAuf_13"),
    VaKueMrnAuf_14("VaKueMrnAuf_14"),
    VaKueMrnAuf_15("VaKueMrnAuf_15"),
    VaKueMrnAuf_16("VaKueMrnAuf_16"),
    VaKueMrnAuf_17("VaKueMrnAuf_17"),
    VaKueMrnAuf_18("VaKueMrnAuf_18"),
    VaKueMrnAuf_19("VaKueMrnAuf_19"),
    VaKueMrnAuf_StornoAenderung_01("VaKueMrnAuf_StornoAenderung_01"),
    VaKueMrnAuf_StornoAenderung_02("VaKueMrnAuf_StornoAenderung_02"),
    VaKueMrnAuf_StornoAenderung_03("VaKueMrnAuf_StornoAenderung_03"),
    VaKueMrnAuf_StornoAenderung_04("VaKueMrnAuf_StornoAenderung_04"),
    VaKueMrnAuf_StornoAenderung_05("VaKueMrnAuf_StornoAenderung_05"),
    VaKueMrnAuf_StornoAenderung_06("VaKueMrnAuf_StornoAenderung_06"),
    VaKueMrnAuf_StornoAenderung_07("VaKueMrnAuf_StornoAenderung_07"),
    VaKueMrnAuf_StornoAenderung_08("VaKueMrnAuf_StornoAenderung_08"),
    VaKueMrnAuf_StornoAenderung_09("VaKueMrnAuf_StornoAenderung_09"),
    VaKueMrnAuf_StornoAenderung_10("VaKueMrnAuf_StornoAenderung_10"),
    VaKueMrnAuf_StornoAenderung_11("VaKueMrnAuf_StornoAenderung_11"),
    VaKueMrnAuf_StornoAenderung_12("VaKueMrnAuf_StornoAenderung_12"),

    VaKueMrnAuf_StornoAufhebung_01("VaKueMrnAuf_StornoAufhebung_01"),
    VaKueMrnAuf_StornoAufhebung_02("VaKueMrnAuf_StornoAufhebung_02"),
    VaKueMrnAuf_StornoAufhebung_03("VaKueMrnAuf_StornoAufhebung_03"),
    VaKueMrnAuf_StornoAufhebung_04("VaKueMrnAuf_StornoAufhebung_04"),
    VaKueMrnAuf_StornoAufhebung_05("VaKueMrnAuf_StornoAufhebung_05"),
    VaKueMrnAuf_StornoAufhebung_06("VaKueMrnAuf_StornoAufhebung_06"),

    VaKueMrnAuf_Terminverschiebung_01("VaKueMrnAuf_TV_01"),
    VaKueMrnAuf_Terminverschiebung_02("VaKueMrnAuf_TV_02"),
    VaKueMrnAuf_Terminverschiebung_03("VaKueMrnAuf_TV_03"),
    VaKueMrnAuf_Terminverschiebung_04("VaKueMrnAuf_TV_04"),
    VaKueMrnAuf_Terminverschiebung_05("VaKueMrnAuf_TV_05"),
    VaKueMrnAuf_Terminverschiebung_06("VaKueMrnAuf_TV_06"),
    VaKueMrnAuf_Terminverschiebung_07("VaKueMrnAuf_TV_07"),
    VaKueMrnAuf_Terminverschiebung_08("VaKueMrnAuf_TV_08"),

    AutomationService_01("AutomationService_01"),
    AutomationService_02("AutomationService_02"),
    AutomationService_03("AutomationService_03"),
    AutomationService_ErlmTv_01("AutomationService_ErlmTv_01"),
    AutomationService_ErlmStrAufh1("AutomationService_ErlmStrAufh1"), // abweichende Schreibweise, da Projektkenner max. 30 Zeichen
    AutomationService_AkmTr_01("AutomationService_AkmTr_01"),

    DonatingAutomationService_01("DonatingAutomationService_01"),
    DonatingAutomationService_02("DonatingAutomationService_02"),
    DonatingAutomationService_03("DonatingAutomationService_03"),
    DonatingAutomationService_04("DonatingAutomationService_04"),

    SchedulerService_01("SchedulerService_01"),
    SchedulerService_02("SchedulerService_02"),
    SchedulerService_03("SchedulerService_03"),
    SchedulerService_04("SchedulerService_04"),
    SchedulerService_05("SchedulerService_05"),
    SchedulerService_06("SchedulerService_06"),

    /**
     * KUEORN aufnehmender Carrier *
     */
    VaKueOrnAufStandardAblauf_01("VaKueOrnAuf_01"),
    VaKueOrnAuf_02("VaKueOrnAuf_02"),
    VaKueOrnAuf_03("VaKueOrnAuf_03"),
    VaKueOrnAuf_04("VaKueOrnAuf_04"),
    VaKueOrnAuf_05("VaKueOrnAuf_05"),
    VaKueOrnAuf_06("VaKueOrnAuf_06"),
    VaKueOrnAuf_StornoAenderung_01("VaKueOrnAuf_StornoAenderung_01"),
    VaKueOrnAuf_StornoAenderung_02("VaKueOrnAuf_StornoAenderung_02"),
    VaKueOrnAuf_StornoAenderung_03("VaKueOrnAuf_StornoAenderung_03"),
    VaKueOrnAuf_StornoAenderung_04("VaKueOrnAuf_StornoAenderung_04"),
    VaKueOrnAuf_StornoAufhebung_01("VaKueOrnAuf_StornoAufhebung_01"),
    VaKueOrnAuf_StornoAufhebung_02("VaKueOrnAuf_StornoAufhebung_02"),
    VaKueOrnAuf_Terminverschiebung_01("VaKueOrnAuf_TV_01"),
    VaKueOrnAuf_Terminverschiebung_02("VaKueOrnAuf_TV_02"),
    VaKueOrnAuf_Terminverschiebung_03("VaKueOrnAuf_TV_03"),
    VaKueOrnAuf_Terminverschiebung_04("VaKueOrnAuf_TV_04"),
    VaKueOrnAuf_Terminverschiebung_05("VaKueOrnAuf_TV_05"),

    /**
     * RRNP aufnehmender Carrier *
     */
    VaRrnpAufStandardAblauf_01("VaRrnpAuf_01"),
    VaRrnpAuf_02("VaRrnpAuf_02"),
    VaRrnpAuf_03("VaRrnpAuf_03"),
    VaRrnpAuf_04("VaRrnpAuf_04"),
    VaRrnpAuf_05("VaRrnpAuf_05"),
    VaRrnpAuf_06("VaRrnpAuf_06"),
    VaRrnpAuf_07("VaRrnpAuf_07"),
    VaRrnpAuf_08("VaRrnpAuf_08"),
    VaRrnpAuf_09("VaRrnpAuf_09"),
    VaRrnpAuf_10("VaRrnpAuf_10"),
    VaRrnpAuf_StornoAenderung_01("VaRrnpAuf_StornoAenderung_01"),
    VaRrnpAuf_StornoAenderung_02("VaRrnpAuf_StornoAenderung_02"),
    VaRrnpAuf_StornoAenderung_03("VaRrnpAuf_StornoAenderung_03"),
    VaRrnpAuf_StornoAenderung_04("VaRrnpAuf_StornoAenderung_04"),
    VaRrnpAuf_StornoAufhebung_01("VaRrnpAuf_StornoAufhebung_01"),
    VaRrnpAuf_StornoAufhebung_02("VaRrnpAuf_StornoAufhebung_02"),

    /*******************************/

    /**
     * KUEMRN abgebender Carrier *
     */
    VaKueMrnAbgStandardAblauf_01("VaKueMrnAbg_01"),
    VaKueMrnAbg_02("VaKueMrnAbg_02"),
    VaKueMrnAbg_03("VaKueMrnAbg_03"),
    VaKueMrnAbg_04("VaKueMrnAbg_04"),
    VaKueMrnAbg_05("VaKueMrnAbg_05"),
    VaKueMrnAbg_06("VaKueMrnAbg_06"),
    VaKueMrnAbg_07("VaKueMrnAbg_07"),
    VaKueMrnAbg_08("VaKueMrnAbg_08"),
    VaKueMrnAbg_09("VaKueMrnAbg_09"),
    VaKueMrnAbg_10("VaKueMrnAbg_10"),
    VaKueMrnAbg_11("VaKueMrnAbg_11"),
    VaKueMrnAbg_12("VaKueMrnAbg_12"),
    VaKueMrnAbg_13("VaKueMrnAbg_13"),
    VaKueMrnAbg_14("VaKueMrnAbg_14"),
    VaKueMrnAbg_15("VaKueMrnAbg_15"),

    VaKueMrnAbgAutoProcessing_01("VaKueMrnAbgAutoProcessing_01"),
    VaKueMrnAbgAutoProcessing_02("VaKueMrnAbgAutoProcessing_02"),
    VaKueMrnAbgAutoProcessing_03("VaKueMrnAbgAutoProcessing_03"),
    VaKueMrnAbgAutoProcessing_04("VaKueMrnAbgAutoProcessing_04"),
    VaKueMrnAbg_Terminverschiebung_01("VaKueMrnAbg_TV_01"),
    VaKueMrnAbg_Terminverschiebung_02("VaKueMrnAbg_TV_02"),
    VaKueMrnAbg_Terminverschiebung_03("VaKueMrnAbg_TV_03"),
    VaKueMrnAbg_Terminverschiebung_04("VaKueMrnAbg_TV_04"),
    VaKueMrnAbg_Terminverschiebung_05("VaKueMrnAbg_TV_05"),
    VaKueMrnAbg_Terminverschiebung_06("VaKueMrnAbg_TV_06"),
    VaKueMrnAbg_Terminverschiebung_07("VaKueMrnAbg_TV_07"),
    VaKueMrnAbg_Terminverschiebung_08("VaKueMrnAbg_TV_08"),
    VaKueMrnAbg_Terminverschiebung_09("VaKueMrnAbg_TV_09"),

    VaKueMrnAbg_StornoAenderung_01("VaKueMrnAbg_StornoAenderung_01"),
    VaKueMrnAbg_StornoAenderung_02("VaKueMrnAbg_StornoAenderung_02"),
    VaKueMrnAbg_StornoAenderung_03("VaKueMrnAbg_StornoAenderung_03"),
    VaKueMrnAbg_StornoAenderung_04("VaKueMrnAbg_StornoAenderung_04"),
    VaKueMrnAbg_StornoAenderung_05("VaKueMrnAbg_StornoAenderung_05"),
    VaKueMrnAbg_StornoAenderung_06("VaKueMrnAbg_StornoAenderung_06"),
    VaKueMrnAbg_StornoAenderung_07("VaKueMrnAbg_StornoAenderung_07"),
    VaKueMrnAbg_StornoAufhebung_01("VaKueMrnAbg_StornoAufhebung_01"),
    VaKueMrnAbg_StornoAufhebung_02("VaKueMrnAbg_StornoAufhebung_02"),
    VaKueMrnAbg_StornoAufhebung_03("VaKueMrnAbg_StornoAufhebung_03"),
    VaKueMrnAbg_StornoAufhebung_04("VaKueMrnAbg_StornoAufhebung_04"),
    VaKueMrnAbg_StornoAufhebung_05("VaKueMrnAbg_StornoAufhebung_05"),
    VaKueMrnAbg_StornoAufhebung_06("VaKueMrnAbg_StornoAufhebung_06"),
    VaKueMrnAbg_StornoAufhebung_07("VaKueMrnAbg_StornoAufhebung_07"),
    VaKueMrnAbg_StornoAufhebung_08("VaKueMrnAbg_StornoAufhebung_08"),
    VaKueMrnAbg_StornoAufhebung_09("VaKueMrnAbg_StornoAufhebung_09"),
    VaKueMrnAbg_StornoAufhebung_10("VaKueMrnAbg_StornoAufhebung_10"),
    VaKueMrnAbg_StornoAufhebung_11("VaKueMrnAbg_StornoAufhebung_11"),
    VaKueMrnAbg_StornoAufhebung_12("VaKueMrnAbg_StornoAufhebung_12"),

    AutoAssignOrderToVaRequest_01("AutoAssignOrderToVaRequest_01"),
    AutoAssignOrderToVaRequest_02("AutoAssignOrderToVaRequest_02"),
    AutoAssignOrderToVaRequest_03("AutoAssignOrderToVaRequest_03"),
    AutoAssignOrderToVaRequest_04("AutoAssignOrderToVaRequest_04"),
    AutoAssignOrderToVaRequest_05("AutoAssignOrderToVaRequest_05"),
    AutoAssignOrderToVaRequest_06("AutoAssignOrderToVaRequest_06"),
    AutoAssignOrderToVaRequest_07("AutoAssignOrderToVaRequest_07"),
    AutoAssignOrderToVaRequest_08("AutoAssignOrderToVaRequest_08"),
    AutoAssignOrderToVaRequest_09("AutoAssignOrderToVaRequest_09"),
    AutoAssignOrderToVaRequest_10("AutoAssignOrderToVaRequest_10"),
    AutoAssignOrderToVaRequest_11("AutoAssignOrderToVaRequest_11"),

    AdminTopic_01("AdminTopic_01"),

    ErrorService_01("ErrorService_01"),

    EscalationReport_Carrier_Overview_01("EscalationReport_Carrier_Overview_01"),
    EscalationReport_CarrierSpecific_01("EscalationReport_CarrierSpecific_01"),
    EscalationReport_Internal_Overview_01("EscalationReport_Internal_Overview_01"),
    EscalationMailVa_01("EscalationMailVa_01"),

    HouseKeeping_01("HouseKeeping_01"),
    HouseKeeping_02("HouseKeeping_02"),
    HouseKeeping_03("HouseKeeping_03"),
    HouseKeeping_04("HouseKeeping_04"),

    LocationService_01("LocationService_01"),
    LocationService_02("LocationService_02"),
    LocationService_03("LocationService_03"),

    ElektraService_01("ElektraService_01"),
    ElektraService_02("ElektraService_02"),
    ElektraService_03("ElektraService_03"),
    ElektraService_04("ElektraService_04"),
    ElektraService_05("ElektraService_05"),

    CustomerServiceAbg_01("CustomerServiceAbg_01"),

    /**
     * KUEORN abgebender Carrier *
     */
    VaKueOrnAbgStandardAblauf_01("VaKueOrnAbg_01"),
    VaKueOrnAbg_02("VaKueOrnAbg_02"),
    VaKueOrnAbg_03("VaKueOrnAbg_03"),
    VaKueOrnAbg_04("VaKueOrnAbg_04"),

    /**
     * RRNP abgebender Carrier *
     */
    VaRrnpAbgStandardAblauf_01("VaRrnpAbg_01"),
    VaRrnpAbg_02("VaRrnpAbg_02"),
    VaRrnpAbg_03("VaRrnpAbg_03"),
    VaRrnpAbg_Terminverschiebung_01("VaRrnpAbg_TV_01"),
    VaRrnpAbg_StornoAufhebung_01("VaRrnpAbg_StornoAufhebung_01"),

    /**
     * KFT TF 1.x : EKPauf und VA-KUE-MRN *
     */
    Kft_1_1_01("Kft_1_1_01"),
    Kft_1_1_02("Kft_1_1_02"),
    Kft_1_2_01("Kft_1_2_01"),
    Kft_1_2_02("Kft_1_2_02"),
    Kft_1_2_03("Kft_1_2_03"),
    Kft_1_2_04("Kft_1_2_04"),
    Kft_1_2_05("Kft_1_2_05"),
    Kft_1_2_06("Kft_1_2_06"),
    Kft_1_2_07("Kft_1_2_07"),
    Kft_1_2_08("Kft_1_2_08"),
    Kft_1_2_09("Kft_1_2_09"),
    Kft_1_2_10("Kft_1_2_10"),
    Kft_1_2_11("Kft_1_2_11"),
    Kft_1_2_12("Kft_1_2_12"),
    Kft_1_2_13("Kft_1_2_13"),

    /**
     * KFT TF 2.x : EKPauf und VA-KUE-ORN *
     */
    Kft_2_1_01("Kft_2_1_01"),
    Kft_2_1_02("Kft_2_1_02"),
    Kft_2_2_01("Kft_2_2_01"),
    Kft_2_2_02("Kft_2_2_02"),
    Kft_2_2_03("Kft_2_2_03"),
    Kft_2_2_04("Kft_2_2_04"),
    Kft_2_2_05("Kft_2_2_05"),
    Kft_2_2_06("Kft_2_2_06"),
    Kft_2_2_07("Kft_2_2_07"),
    Kft_2_2_08("Kft_2_2_08"),
    Kft_2_2_09("Kft_2_2_09"),
    Kft_2_2_10("Kft_2_2_10"),
    Kft_2_2_11("Kft_2_2_11"),

    /**
     * KFT TF 3.x : EKPauf und VA-RRNP *
     */
    Kft_3_1_01("Kft_3_1_01"),
    Kft_3_1_02("Kft_3_1_02"),
    Kft_3_2_01("Kft_3_2_01"),
    Kft_3_2_02("Kft_3_2_02"),
    Kft_3_2_03("Kft_3_2_03"),
    Kft_3_2_04("Kft_3_2_04"),
    Kft_3_2_05("Kft_3_2_05"),
    Kft_3_2_06("Kft_3_2_06"),
    Kft_3_2_07("Kft_3_2_07"),

    /**
     * KFT TF 4.x : EKPauf und STR-AUF *
     */
    Kft_4_1_01("Kft_4_1_01"),
    Kft_4_2_01("Kft_4_2_01"),
    Kft_4_2_02("Kft_4_2_02"),

    /**
     * KFT TF 5.x : EKPauf und STR-AEN *
     */
    Kft_5_1_01("Kft_5_1_01"),
    Kft_5_2_01("Kft_5_2_01"),
    Kft_5_2_02("Kft_5_2_02"),

    /**
     * KFT TF 6.x : TVS (EKPauf sendet Auftrag) *
     */
    Kft_6_1_01("Kft_6_1_01"),
    Kft_6_2_01("Kft_6_2_01"),
    Kft_6_2_02("Kft_6_2_02"),

    /**
     * KFT TF 7.x : STR-AUF (EKPauf sendet Auftrag) *
     */
    Kft_7_1_01("Kft_7_1_01"),
    Kft_7_2_01("Kft_7_2_01"),
    Kft_7_2_02("Kft_7_2_02"),

    /**
     * KFT TF 8.x : STR-AEN (EKPauf sendet Auftrag) *
     */
    Kft_8_1_01("Kft_8_1_01"),
    Kft_8_2_01("Kft_8_2_01"),
    Kft_8_2_02("Kft_8_2_02"),

    /**
     * KFT TF 9.x : EKPabg und VA-KUE-MRN *
     */
    Kft_9_1_01("Kft_9_1_01"),
    Kft_9_1_02("Kft_9_1_02"),
    Kft_9_2_01("Kft_9_2_01"),
    Kft_9_2_02("Kft_9_2_02"),
    Kft_9_2_03("Kft_9_2_03"),
    Kft_9_2_04("Kft_9_2_04"),
    Kft_9_2_05("Kft_9_2_05"),
    Kft_9_2_06("Kft_9_2_06"),
    Kft_9_2_07("Kft_9_2_07"),
    Kft_9_2_08("Kft_9_2_08"),
    Kft_9_2_09("Kft_9_2_09"),
    Kft_9_2_10("Kft_9_2_10"),
    Kft_9_2_11("Kft_9_2_11"),
    Kft_9_2_12("Kft_9_2_12"),
    Kft_9_2_13("Kft_9_2_13"),

    /**
     * KFT TF 10.x : EKPabg und VA-KUE-ORN *
     */
    Kft_10_1_01("Kft_10_1_01"),
    Kft_10_1_02("Kft_10_1_02"),
    Kft_10_2_01("Kft_10_2_01"),
    Kft_10_2_02("Kft_10_2_02"),
    Kft_10_2_03("Kft_10_2_03"),
    Kft_10_2_04("Kft_10_2_04"),
    Kft_10_2_05("Kft_10_2_05"),
    Kft_10_2_06("Kft_10_2_06"),
    Kft_10_2_07("Kft_10_2_07"),
    Kft_10_2_08("Kft_10_2_08"),
    Kft_10_2_09("Kft_10_2_09"),
    Kft_10_2_10("Kft_10_2_10"),
    Kft_10_2_11("Kft_10_2_11"),
    Kft_10_2_12("Kft_10_2_12"),
    Kft_10_2_13("Kft_10_2_13"),

    /**
     * KFT TF 11.x : EKPabg und VA-RRNP *
     */
    Kft_11_1_01("Kft_11_1_01"),
    Kft_11_1_02("Kft_11_1_02"),
    Kft_11_2_01("Kft_11_2_01"),
    Kft_11_2_02("Kft_11_2_02"),
    Kft_11_2_03("Kft_11_2_03"),
    Kft_11_2_04("Kft_11_2_04"),
    Kft_11_2_05("Kft_11_2_05"),
    Kft_11_2_06("Kft_11_2_06"),
    Kft_11_2_07("Kft_11_2_07"),

    /**
     * KFT TF 12.x : EKPabg und STR-AUF (M-net empfängt) *
     */
    Kft_12_1_01("Kft_12_1_01"),
    Kft_12_2_01("Kft_12_2_01"),
    Kft_12_2_02("Kft_12_2_02"),

    /**
     * KFT TF 13.x : EKPabg und STR-AEN *
     */
    Kft_13_1_01("Kft_13_1_01"),
    Kft_13_2_01("Kft_13_2_01"),
    Kft_13_2_02("Kft_13_2_02"),

    /**
     * KFT TF 14.x : EKPabg und TVS-VA (M-net empfängt) *
     */
    Kft_14_1_01("Kft_14_1_01"),
    Kft_14_2_01("Kft_14_2_01"),
    Kft_14_2_02("Kft_14_2_02"),

    /**
     * KFT TF 15.x : EKPabg und STR-AUF (M-net versendet) *
     */
    Kft_15_1_01("Kft_15_1_01"),
    Kft_15_2_01("Kft_15_2_01"),
    Kft_15_2_02("Kft_15_2_02"),

    /**
     * KFT TF 16.x : EKPabg und STR-AEN (M-net versendet) *
     */
    Kft_16_1_01("Kft_16_1_01"),
    Kft_16_2_01("Kft_16_2_01"),
    Kft_16_2_02("Kft_16_2_02");

    private String simulatorName;
    private String kftName;

    /**
     * Constructor used when simulator and kft use case name do not differ.
     *
     * @param useCaseName
     */
    private WbciSimulatorUseCase(String useCaseName) {
        this(useCaseName, useCaseName);
    }

    /**
     * Constructor using separate simulator and kft use case names. Which name to pick is decided during runtime by
     * system property evaluation (@see WbciSimulatorUseCase.getName()).
     *
     * @param simulatorName
     * @param kftName
     */
    private WbciSimulatorUseCase(String simulatorName, String kftName) {
        this.simulatorName = simulatorName;
        this.kftName = kftName;
    }

    /**
     * Gets the use case name according to acceptance test mode system property value. Either we do test against the
     * wbci simulator in a internal acceptance test scenario or we have to use the kft test system provided by Telekom
     * in a real kft test scenario.
     *
     * @return
     */
    public String getName() {
        String mode = System.getProperty("wbci.acceptance.test.mode");

        if ((mode != null) && mode.equalsIgnoreCase("kft")) {
            return kftName;
        }
        return simulatorName;
    }

}
