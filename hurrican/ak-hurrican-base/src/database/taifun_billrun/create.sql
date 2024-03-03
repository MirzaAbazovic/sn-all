create table RTB_BILL_200610(
	BILL_NO INT(10) not null, 
	BILL_ID VARCHAR (20), 
	INVOICE_DATE DATE default null, 
	CUSTOMER__NO INT(10) not null, 
	ACCOUNT_NO INT(10) not null, 
	CHARGED_FROM DATE default null, 
	CHARGED_TO DATE default null, 
	AMOUNT DECIMAL (18, 2) not null, 
	CURRENCY_ID CHAR (3) not null, 
	PAYMENT_METHOD VARCHAR (5) not null, 
	STATE VARCHAR (20) default 'CREATED' not null, 
	REMAINING_AMOUNT DECIMAL(18,2) not null,
    PAID_ON DATE default null,
    RELEASED_AT DATE default null,
    RELEASED_BY INT(10),
    COMMISSIONED_AT DATE default null,
    COMMISSIONED_BY INT(10),
    COMMISSION_ID VARCHAR (20),
    DUNNING_LEVEL INT(3) default 0 not null,
    DUNNING_LEVEL_SINCE DATE default null,
    DUNNED_L1_AT DATE default null,
    DUNNED_L1_BY INT(10),
    DUNNED_L2_AT DATE default null,
    DUNNED_L2_BY INT(10),
    CANCEL_ID VARCHAR (20),
    CANCEL_CAUSE_NO INT(10),
    CANCELLED_AT DATE default null,
    CANCELLED_BY INT(10),
    WRITE_OFF_ID VARCHAR (20),
    WRITE_OFF_CAUSE_NO INT(10),
    WRITTEN_OFF_AT DATE default null,
    WRITTEN_OFF_BY INT(10),
    constraint PK_RTB_BILL_200610 primary key (BILL_NO)
);

create table A_PRICE_SUM_200610(
    KUNDE__NO INT(10) not null,
    AUFTRAG_NO INT(10) not null,
    NODE_CONN_NO INT(10) not null,
    PRICE_TYPE_NO INT(10) not null,
    BILL_SPEC_SELECTION INT(10) not null,
    COST_SUM DECIMAL(18, 2) not null,
    CALL_LENGTH_SUM DECIMAL(19, 3) not null,
    TAX_TIME_SUM DECIMAL(19, 3) not null,
    NO_CALLS DECIMAL(16) not null,
    GROUP_QUALIFIER VARCHAR (20)
);

create table RTB_BILL_ITEM_200610(
    ITEM_NO INT(10) not null,
    BILL_NO INT(10) not null,
    ORIG_PERIOD INT(10) not null,
    ITEM_TYPE_NO INT(10) not null,
    CUSTOMER__NO INT(10) not null,
    ORDER__NO INT(10) not null,
    PRODUCT__NO INT(10) not null,
    ORDER_ITEM__NO INT(10),
    SERVICE_NO INT(10),
    DISCOUNT_NO INT(10),
    CHARGED_FROM DATE default null,
    CHARGED_TO DATE default null,
    QUANTITY DECIMAL(18, 4),
    PRICE DECIMAL(18, 2),
    AMOUNT DECIMAL(18, 2) not null,
    CURRENCY_ID CHAR (3) not null,
    constraint PK_RTB_BILL_ITEM_200610 primary key (ITEM_NO)
);


create table RTB_BILL_200610_mig(
	BILL_NO INT(10) not null, 
	BILL_ID VARCHAR (20), 
	INVOICE_DATE DATE default null, 
	CUSTOMER__NO INT(10) not null, 
	ACCOUNT_NO INT(10) not null, 
	CHARGED_FROM DATE default null, 
	CHARGED_TO DATE default null, 
	AMOUNT DECIMAL (18, 2) not null, 
	CURRENCY_ID CHAR (3) not null, 
	PAYMENT_METHOD VARCHAR (5) not null, 
	STATE VARCHAR (20) default 'CREATED' not null, 
	REMAINING_AMOUNT DECIMAL(18,2) not null,
    PAID_ON DATE default null,
    RELEASED_AT DATE default null,
    RELEASED_BY INT(10),
    COMMISSIONED_AT DATE default null,
    COMMISSIONED_BY INT(10),
    COMMISSION_ID VARCHAR (20),
    DUNNING_LEVEL INT(3) default 0 not null,
    DUNNING_LEVEL_SINCE DATE default null,
    DUNNED_L1_AT DATE default null,
    DUNNED_L1_BY INT(10),
    DUNNED_L2_AT DATE default null,
    DUNNED_L2_BY INT(10),
    CANCEL_ID VARCHAR (20),
    CANCEL_CAUSE_NO INT(10),
    CANCELLED_AT DATE default null,
    CANCELLED_BY INT(10),
    WRITE_OFF_ID VARCHAR (20),
    WRITE_OFF_CAUSE_NO INT(10),
    WRITTEN_OFF_AT DATE default null,
    WRITTEN_OFF_BY INT(10),
    constraint PK_RTB_BILL_200610 primary key (BILL_NO)
);

create table A_PRICE_SUM_200610_mig(
    KUNDE__NO INT(10) not null,
    AUFTRAG_NO INT(10) not null,
    NODE_CONN_NO INT(10) not null,
    PRICE_TYPE_NO INT(10) not null,
    BILL_SPEC_SELECTION INT(10) not null,
    COST_SUM DECIMAL(18, 2) not null,
    CALL_LENGTH_SUM DECIMAL(19, 3) not null,
    TAX_TIME_SUM DECIMAL(19, 3) not null,
    NO_CALLS DECIMAL(16) not null,
    GROUP_QUALIFIER VARCHAR (20)
);

create table RTB_BILL_ITEM_200610_mig(
    ITEM_NO INT(10) not null,
    BILL_NO INT(10) not null,
    ORIG_PERIOD INT(10) not null,
    ITEM_TYPE_NO INT(10) not null,
    CUSTOMER__NO INT(10) not null,
    ORDER__NO INT(10) not null,
    PRODUCT__NO INT(10) not null,
    ORDER_ITEM__NO INT(10),
    SERVICE_NO INT(10),
    DISCOUNT_NO INT(10),
    CHARGED_FROM DATE default null,
    CHARGED_TO DATE default null,
    QUANTITY DECIMAL(18, 4),
    PRICE DECIMAL(18, 2),
    AMOUNT DECIMAL(18, 2) not null,
    CURRENCY_ID CHAR (3) not null,
    constraint PK_RTB_BILL_ITEM_200610 primary key (ITEM_NO)
);


create table UDR_OK_200610(
    SOURCE_SYSTEM_ID VARCHAR (10) not null,
    SS_FILE_NO INT(10) not null,
    SS_UDR_ID VARCHAR (100),
    SS_UDR_DATETIME DATE default null,
    SS_ORDER_ID VARCHAR (10) not null,
    SS_SERVICE_ID VARCHAR (10) not null,
    SS_BMONTH CHAR (6) not null,
    SS_QUANTITY DECIMAL (18, 4),
    SS_QUANTITY_UNIT VARCHAR (10),
    SS_COST DECIMAL(18, 4),
    SS_COST_UNIT VARCHAR (10),
    UDR_BATCH_NO INT(10) not null,
    UDR_ORDER_NO INT(10) not null,
    UDR_SERVICE_CODE VARCHAR (16) not null,
    UDR_QUANTITY DECIMAL(18, 4),
    UDR_QUANTITY_UNIT VARCHAR (10),
    UDR_COST DECIMAL(18, 4),
    UDR_COST_UNIT VARCHAR (10),
    USER_DEF_1 VARCHAR (255),
    USER_DEF_2 VARCHAR (255),
    USER_DEF_3 VARCHAR (255),
    USER_DEF_4 VARCHAR (255),
    USER_DEF_5 VARCHAR (255)
);

create table A_CDR_FILTERED_200610(
    CDR_FILE_NO INT(10) not null,
    FILE_OFFSET INT(10) not null,
    CONN_ID INT(10),
    A_NUMBER VARCHAR (20),
    B_NUMBER VARCHAR (18),
    C_NUMBER VARCHAR (18),
    A2_NUMBER VARCHAR (16),
    CALL_DATETIME VARCHAR (17),
    CALL_LENGTH DECIMAL(13, 3),
    NO_ANSWER CHAR (1),
    FIRST_RECORD CHAR (1),
    LAST_RECORD CHAR (1),
    SERVICE_INFO INT(3) not null,
    CENTREX CHAR (1),
    VPN INT(10),
    CAUSE_VALUE INT(5),
    IND_INTERCONN INT(3),
    IND_SFC INT(10),
    TRUNK_GROUP_IN CHAR (9),
    TRUNK_GROUP_OUT CHAR (9),
    DURATION_DIAL DECIMAL(13, 3),
    DURATION_ANSWER DECIMAL(13, 3),
    NODE_CONN_NO INT(10),
    TAX_TIME DECIMAL(13, 3),
    COST DECIMAL(18,2),
    TARIFF_TRANSIT CHAR (1),
    USER_DEF_1 VARCHAR (255),
    USER_DEF_2 VARCHAR (255),
    USER_DEF_3 VARCHAR (255),
    USER_DEF_4 VARCHAR (255),
    USER_DEF_5 VARCHAR (255),
    KUNDE__NO INT(10),
    AUFTRAG_NO INT(10),
    ERROR_DESC VARCHAR (255) not null
);

create table A_CDR_OK_200610_0(
    CDR_FILE_NO INT(10) not null,
    FILE_OFFSET INT(10) not null,
    CONN_ID INT(10) not null,
    A_NUMBER VARCHAR (20) not null,
    B_NUMBER VARCHAR (18) not null,
    C_NUMBER VARCHAR (18),
    A2_NUMBER VARCHAR (16),
    CALL_DATETIME VARCHAR (17) not null,
    CALL_LENGTH DECIMAL (13, 3) not null,
    NO_ANSWER CHAR (1) not null,
    FIRST_RECORD CHAR (1) not null,
    LAST_RECORD CHAR (1) not null,
    SERVICE_INFO INT(3) not null,
    CENTREX CHAR (1),
    VPN INT(10),
    CAUSE_VALUE INT(5),
    IND_INTERCONN INT(3),
    IND_SFC INT(10),
    TRUNK_GROUP_IN CHAR (9),
    TRUNK_GROUP_OUT CHAR (9),
    DURATION_DIAL DECIMAL (13, 3),
    DURATION_ANSWER DECIMAL (13, 3),
    NODE_CONN_NO INT(10) not null,
    TAX_TIME DECIMAL (13, 3) not null,
    COST DECIMAL(18,2) not null,
    TARIFF_TRANSIT CHAR (1) not null,
    USER_DEF_1 VARCHAR (255),
    USER_DEF_2 VARCHAR (255),
    USER_DEF_3 VARCHAR (255),
    USER_DEF_4 VARCHAR (255),
    USER_DEF_5 VARCHAR (255),
    KUNDE__NO INT(10) not null,
    AUFTRAG_NO INT(10) not null
);

CREATE INDEX IX_PRICESUM_AUFTRAG ON A_PRICE_SUM_200610 (AUFTRAG_NO ASC);
CREATE INDEX IX_PRICESUM_KUNDE ON A_PRICE_SUM_200610 (KUNDE__NO ASC);
CREATE INDEX IX_RTBBILL_KUNDE ON RTB_BILL_200610 (CUSTOMER__NO ASC);
CREATE INDEX IX_RTBBILLITEM_KUNDE ON RTB_BILL_ITEM_200610 (CUSTOMER__NO ASC);
CREATE INDEX IX_RTBBILLITEM_BILLNO ON RTB_BILL_ITEM_200610 (BILL_NO ASC);

CREATE INDEX IX_PRICESUM_AUFTRAG ON A_PRICE_SUM_200610_mig (AUFTRAG_NO ASC);
CREATE INDEX IX_PRICESUM_KUNDE ON A_PRICE_SUM_200610_mig (KUNDE__NO ASC);
CREATE INDEX IX_RTBBILL_KUNDE ON RTB_BILL_200610_mig (CUSTOMER__NO ASC);
CREATE INDEX IX_RTBBILLITEM_KUNDE ON RTB_BILL_ITEM_200610_mig (CUSTOMER__NO ASC);
CREATE INDEX IX_RTBBILLITEM_BILLNO ON RTB_BILL_ITEM_200610_mig (BILL_NO ASC);
