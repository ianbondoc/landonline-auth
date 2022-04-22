package nz.govt.linz.jpa

/**
 * Mapping of profiles
 */
enum class Profile(val id: String, val description: String) {
    /** Survey Approver */
    SURVEY_APPROVER("01 - AS", "Survey Approver"),

    /** Customer Service Representative & Read only for Firm and User details */
    CUSTOMER_SERVICE_REPRESENTATIVE(
        "02 - CSR",
        "Customer Service Representative & Read only for Firm and User details"
    ),

    /** Help Desk Technical Advisor & Read only firm and user details & check 3 CRI  */
    HELP_DESK_TECHNICAL_ADVISOR(
        "03 - HDTA",
        "Help Desk Technical Advisor & Read only firm and user details & check 3 CRI "
    ),

    /** Property Rights Analyst - Titles */
    PROPERTY_RIGHTS_ANALYST_T("04 - PRA-T", "Property Rights Analyst - Titles"),

    /** Operations Support Representative */
    OPERATIONS_SUPPORT_REPRESENTATIVE("05 - OSR", "Operations Support Representative"),

    /** Quality Control Coordinator */
    QUALITY_CONTROL_COORDINATOR("06 - QCC", "Quality Control Coordinator"),

    /** Regional Manager */
    REGIONAL_MANAGER("07 - RM", "Regional Manager"),

    /** Operations Manager - Regions */
    OPERATIONS_MANAGER_R("08 - OM-R", "Operations Manager - Regions"),

    /** Regional Regulatory - Survey */
    REGIONAL_REGULATORY_S("09 - RR-S", "Regional Regulatory - Survey"),

    /** Regional Regulatory - Titles */
    REGIONAL_REGULATORY_T("10 - RR-T", "Regional Regulatory - Titles"),

    /** Property Rights Analyst - Survey */
    PROPERTY_RIGHTS_ANALYST_S("11 - PRA-S", "Property Rights Analyst - Survey"),

    /** Systems Support Representative & read only firm and user details. */
    SYSTEMS_SUPPORT_REPRESENTATIVE("12 - SSR", "Systems Support Representative & read only firm and user details."),

    /** Team Performance Coordinator & Read only Firm and User details */
    TEAM_PERFORMANCE_COORDINATOR("13 - TPC", "Team Performance Coordinator & Read only Firm and User details"),

    /** Property Rights Analyst - Capture */
    PROPERTY_RIGHTS_ANALYST_C("14 - PRA-C", "Property Rights Analyst - Capture"),

    /** Property Rights Analyst - Network */
    PROPERTY_RIGHTS_ANALYST_N("15 - PRA-N", "Property Rights Analyst - Network"),

    /** Remote - Spatial */
    REMOTE("19 - REMOTE", "Remote - Spatial"),

    /** Generic non-regional profile */
    GENERIC_NON_REGIONAL("20 - GNRP", "Generic non-regional profile"),

    /** Technical Services Advisor */
    TECHNICAL_SERVICES_ADVISOR("21 - TSAD", "Technical Services Advisor"),

    /** Systems Management Advisor */
    SYSTEMS_MANAGEMENT_ADVISOR("22 - SMAD", "Systems Management Advisor"),

    /** Assurance Analyst */
    ASSURANCE_ANALYST("23 - ASAN", "Assurance Analyst"),

    /** Solicitor */
    SOLICITOR("24 - SOLR", "Solicitor"),

    /** Systems Change */
    SYSTEMS_CHANGE("25 - SYSC", "Systems Change"),

    /** System Administrator */
    ADMIN("26 - ADMIN", "System Administrator"),

    /** Amended CALL profile (Batch Server access removed) */
    CALL("26A - CALL", "Amended CALL profile (Batch Server access removed)"),

    /** Geodetic Advisor/Auditor */
    GEO_AA("28 - GEO AA", "Geodetic Advisor/Auditor"),

    /** Geodetic Maintenance */
    GEO_AUTH("29 - GEO AUTH", "Geodetic Maintenance"),

    /** Cadastral Advisor/Auditor */
    CAD_AA("30 - CAD AA", "Cadastral Advisor/Auditor"),

    /** Geodetic Technical - CMR */
    GEO_TEAM("32 - GEO TEAM", "Geodetic Technical - CMR"),

    /** Geodetic Contracts */
    GEO_REC("33 - GEO REC", "Geodetic Contracts"),

    /** Public */
    PUBLIC("34 - PUBLIC", "Public"),

    /** Priority titles report user */
    PRIORITY_TITLES_USER("35- PRIORITY TITLES", "Priority titles report user"),

    /** Temporary add user rights */
    ADD_USER("36-ADD USER (TEMP)", "Temporary add user rights"),

    /** Image import */
    IMAGE_IMPORT("37 - IMAGE_IMPORT", "Image import"),

    /** Run but don't Authorise Adjustments */
    GEO_MAIN_NO_AUTH("38 - GEO MAIN(-AUTH)", "Run but don't Authorise Adjustments"),

    /** Maintain Network and Authorise Adjustment */
    GEO_MAIN("39 - GEO MAIN", "Maintain Network and Authorise Adjustment"),

    /** Remote - Aspatial */
    REMOTE_ASPATIAL("40 - REMOTE ASPATIAL", "Remote - Aspatial"),

    /** Asscess to additional field CCL_S14a required for selected PRA-C staff. */
    PROPERTY_RIGHTS_ANALYST_C1(
        "41 - PRA-C1",
        "Asscess to additional field CCL_S14a required for selected PRA-C staff."
    ),

    /** Access to additional field CMN_S17 required for selected PRA-N staff. */
    PROPERTY_RIGHTS_ANALYST_N1("42 - PRA-N1", "Access to additional field CMN_S17 required for selected PRA-N staff."),

    /** Desktop Support */
    DESKTOP("43 - DESKTOP", "Desktop Support"),

    /** To Maintain the Plan Images in LOL */
    MAINTAIN_IMAGES("44 - MAINTAIN IMAGES", "To Maintain the Plan Images in LOL"),

    /** To Print Barcodes of Plan images in LOL */
    PRINT_IMG_BARCODE("45 - PRINT IMG BCODE", "To Print Barcodes of Plan images in LOL"),

    /** External Notices */
    EXTERNAL_NOTICES("49 - EXT NOTICES", "External Notices"),

    /** External Titles Users */
    EXTERNAL_TITLES_USER("50 - EXT TITLE", "External Titles Users"),

    /** External Titles Users, Notice of Sale */
    NOTICE_OF_SALE("50A - Notice of Sale", "External Titles Users, Notice of Sale"),

    /** External Survey Users */
    EXTERNAL_SURVEY_USER("51 - EXT SURVEY", "External Survey Users"),

    /** External Search Users */
    EXTERNAL_SEARCH_USER("53 - EXT SEARCH", "External Search Users"),

    /** External System Manager */
    EXTERNAL_SYSTEM_MANAGER("54 - EXT SYS MGR", "External System Manager"),

    /** Full Administrator access */
    CRS_ADMIN("55 - CRS ADMIN", "Full Administrator access"),

    /** Reconcile Images report */
    RECONCILE_IMAGE("56 - RECONCILE IMAGE", "Reconcile Images report"),

    /** TA Certification User */
    TA_CERT("57 - TA CERT", "TA Certification User"),

    /** Barcode Maintenance - Replace Images */
    BARCODE_MAINTENANCE("58 - BARCODE MAINT", "Barcode Maintenance - Replace Images"),

    /** Special access to fees to allow editing of fees for completed transactions */
    SPECIAL_FEE_ACCESS(
        "59-SPEC FEE ACCESS",
        "Special access to fees to allow editing of fees for completed transactions"
    ),

    /** Access to view fees the reports of customers */
    CUSTOMER_FEES("60 - CUSTOMER FEES", "Access to view fees the reports of customers"),

    /** Access to Sys Admn\\Workflow menu for Batch Schedule Items and BSA screen */
    BATCH_ADMIN("61 - BATCHADM", "Access to Sys Admn\\Workflow menu for Batch Schedule Items and BSA screen"),

    /** Enable Client and Firm creation and amendments */
    CLIENT_ADMIN("62 - CLIENTADM", "Enable Client and Firm creation and amendments"),

    /** Internal Search profile mimics external Search */
    INTERNAL_SEARCH("63 - INTERNAL SEARCH", "Internal Search profile mimics external Search"),

    /** CSR & Change Password access */
    PASSWORD_CSR("64 - PASSWORD_CSR", "CSR & Change Password access"),
    CSR_PLUS(
        "65 - CSR PLUS",
        "Customer Services Representative & Read only for Firm and User details plus ability to save edits"
    ),

    /** Ability to Merge Parcels */
    MERGE_PARCELS("66 - MERGE PARCELS", "Ability to Merge Parcels"),

    /** Error Log Access in the Notification Search Screen */
    NOTF_ERR_ACCESS("67 - NOTF ERR ACCESS", "Error Log Access in the Notification Search Screen"),

    /** Ability for Warranted Staff to modify Images associated to Instruments */
    EDIT_INSTRUMENT("68 - EDIT INSTRUMENT", "Ability for Warranted Staff to modify Images associated to Instruments"),

    /** Ability for Warranted Staff Level 40 staff to uncancel titles */
    UNCANCEL_TITLE("69 - UNCANCEL TITLE", "Ability for Warranted Staff Level 40 staff to uncancel titles"),

    /** Read only access to QCC screens */
    QCC_READ_ONLY("70 - QCC READ ONLY", "Read only access to QCC screens"),

    /** DVL - Full access to record DVL items. Search DVL Titles */
    DVL_FULL_ACCESS("72 - DVL FULL ACCESS", "DVL - Full access to record DVL items. Search DVL Titles"),

    /** DVL - Read only access of DVL items. Search DVL Titles */
    DVL_SEARCH("73 - DVL SEARCH", "DVL - Read only access of DVL items. Search DVL Titles"),

    /** To allow job complexity to be changed via the View Job Tasks screen */
    CHG_COMPLEXITY("74 - CHG COMPLEXITY", "To allow job complexity to be changed via the View Job Tasks screen"),

    /** To allow a title status to be updated via DD subsystem */
    MOD_TTL_STATUS("75 - MOD TTL STATUS", "To allow a title status to be updated via DD subsystem"),

    /** Special access to IRD tax statement Supporting Documents */
    IRD_TAX_STATEMENT("76 - IRD TAX STATMNT", "Special access to IRD tax statement Supporting Documents"),
    IRD_TAX_REQUEST(
        "77 - IRD TAX REQUEST",
        "Special access to Correction of tax info requests in CRI, Searches and CDD_S09 via the DD menu"
    ),
    QCC_IRD_TAX(
        "78 - QCC IRD TAX",
        "Special access to QC IRD info - via CDD_S09 in a dealing and a SUD in CRI or Searches"
    ),

    /** Performance monitoring */
    CPM_MONITOR("79 - CPM MONITOR", "Performance monitoring"),

    /** Use spatial web browser insead of map objects for CDE and CMR */
    SPATIAL_BROWSER_80("80 - Spatial Browser", "Use spatial web browser insead of map objects for CDE and CMR"),

    /** Use spatial web browser insead of map objects for CMN */
    SPATIAL_BROWSER_81("81 - Spatial Browser", "Use spatial web browser insead of map objects for CMN"),

    /** Use spatial web browser insead of map objects for CTG */
    SPATIAL_BROWSER_82("82 - Spatial Browser", "Use spatial web browser insead of map objects for CTG"),

    /** Use spatial web browser insead of map objects for CPL */
    SPATIAL_BROWSER_83("83 - Spatial Browser", "Use spatial web browser insead of map objects for CPL"),

    /** Use spatial web browser insead of map objects for CPG */
    SPATIAL_BROWSER_84("84 - Spatial Browser", "Use spatial web browser insead of map objects for CPG"),

    /** CMR S14 temp set up */
    CMR_S14TEMP("CMR_S14TEMP", "CMR S14 temp set up"),

    /** CNO_S03 Temp Print Dup Title */
    CNO_S03_TEMP("CNO_S03 TEMP", "CNO_S03 Temp Print Dup Title"),

    /** CPR Target administration */
    CPR_TARGET_ADMIN("CPR TARGET ADMIN", "CPR Target administration"),

    /** Entrust administrator for maintaining smart credentials. */
    ENTRUST_ADMIN("ENTRUST ADMIN", "Entrust administrator for maintaining smart credentials."),

    /** bla */
    EXAMPLE_PROFILE("EXAMPLE PROFILE", "bla"),

    /** Maintain auto cadastral adj methods */
    MAIN_AUTO_ADJ_METH("MAIN AUTO ADJ METH", "Maintain auto cadastral adj methods"),

    /** Temporary access to Notice component for analysys of Duplicate Titles issues. */
    NOTICE_TEMP("NOTICE-TEMP", "Temporary access to Notice component for analysys of Duplicate Titles issues."),

    /** National Processing Manager */
    NATIONAL_PROCESSING_MANAGER("NPM", "National Processing Manager"),

    /** National Queue Processing Manager */
    NATIONAL_QUEUE_PROCESSING_MANAGER("NQP", "National Queue Processing Manager"),

    /** PWC Production profile */
    PWC_PROD("PWC PROD", "PWC Production profile"),

    /** RA administrator for assigning RA ADMIN and ENTRUST ADMIN profiles. */
    RA_ADMIN("RA ADMIN", "RA administrator for assigning RA ADMIN and ENTRUST ADMIN profiles."),

    /** Record Work in Progress - temp ID created on 14.12.00call 9261 linked to 9142 for Wade Byers. */
    REC_WIP("REC_WIP", "Record Work in Progress - temp ID created on 14.12.00call 9261 linked to 9142 for Wade Byers."),

    /** RGL QC Access */
    RGL_QCC("RGL_QCC", "RGL QC Access"),

    /** System Support, Diagnostics and Tracing Logging */
    SUPPORT_DEBUGGING("SUPPORT - DEBUGGING", "System Support, Diagnostics and Tracing Logging"),

    /** Access to Flag Urgent button in the Workflow screen */
    WORKFLOW_URGENCY("WORKFLOW_URGENCY", "Access to Flag Urgent button in the Workflow screen");

    override fun toString(): String = id

    companion object {
        private val mappedById: Map<String, Profile> by lazy { values().associateBy { it.id } }
        fun byId(id: String) = requireNotNull(mappedById[id]) { "Not a valid Profile: '$id'" }
        fun byIds(ids: Collection<String>): List<Profile> =
            ids.map { requireNotNull(mappedById[it]) { "Not a valid Profile: '$it'" } }
    }
}
