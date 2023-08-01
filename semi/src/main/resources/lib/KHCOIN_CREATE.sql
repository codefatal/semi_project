DROP TABLE "MYPAGE";
DROP TABLE "PRICES";
DROP TABLE "TRADE_TEST";
DROP TABLE "COINS";
DROP TABLE "SITE_USER";

DROP SEQUENCE IDENTITY;
CREATE SEQUENCE IDENTITY;

CREATE TABLE "SITE_USER" (
	"ID"	NUMBER		DEFAULT "SPRING"."IDENTITY".nextval NOT NULL,
	"USERNAME"	VARCHAR2(255)	UNIQUE NOT NULL,
	"PASSWORD"	VARCHAR2(255)	NOT NULL,
	"EMAIL"	VARCHAR2(255)		UNIQUE NOT NULL
);

COMMENT ON COLUMN "SITE_USER"."ID" IS '식별ID';

COMMENT ON COLUMN "SITE_USER"."USERNAME" IS '유저네임';

COMMENT ON COLUMN "SITE_USER"."PASSWORD" IS '패스워드 입력';

COMMENT ON COLUMN "SITE_USER"."EMAIL" IS '이메일';


CREATE TABLE "COINS" (
	"COINCODE"	VARCHAR2(10)		NOT NULL,
	"COINNAME"	VARCHAR2(100)		NOT NULL
);

COMMENT ON COLUMN "COINS"."COINCODE" IS '코인코드';

COMMENT ON COLUMN "COINS"."COINNAME" IS '코인이름';


CREATE TABLE "PRICES" (
	"PNUM"	NUMBER		NOT NULL,
	"COINCODE"	VARCHAR2(10)		NOT NULL,
	"PRICE"	FLOAT		NOT NULL,
	"VOLUMES"	FLOAT		NOT NULL,
	"DATES"	TIMESTAMP		NOT NULL
);

COMMENT ON COLUMN "PRICES"."PNUM" IS 'API수신순서';

COMMENT ON COLUMN "PRICES"."COINCODE" IS '코인코드';

COMMENT ON COLUMN "PRICES"."PRICE" IS '코인가격';

COMMENT ON COLUMN "PRICES"."VOLUMES" IS '거래량';

COMMENT ON COLUMN "PRICES"."DATES" IS '수신시간';


CREATE TABLE "TRADE_TEST" (
	"ID"	NUMBER		NOT NULL,
	"COINCODE"	VARCHAR2(10)		NOT NULL,
	"TRADE_TYPE"	NUMBER		NOT NULL,
	"TRADE_PRICE"	NUMBER		NOT NULL,
	"TRADE_ITEM"	FLOAT		NOT NULL,
	"TRADE_MONEY"	NUMBER		NOT NULL,
	"TRADE_COUNT"	NUMBER	DEFAULT 1	NOT NULL
);

COMMENT ON COLUMN "TRADE_TEST"."ID" IS '식별ID';

COMMENT ON COLUMN "TRADE_TEST"."COINCODE" IS '코인코드';

COMMENT ON COLUMN "TRADE_TEST"."TRADE_TYPE" IS '구매/판매';

COMMENT ON COLUMN "TRADE_TEST"."TRADE_PRICE" IS '주문가격';

COMMENT ON COLUMN "TRADE_TEST"."TRADE_ITEM" IS '주문수량';

COMMENT ON COLUMN "TRADE_TEST"."TRADE_MONEY" IS '총주문금액';

COMMENT ON COLUMN "TRADE_TEST"."TRADE_COUNT" IS '유저당 거래횟수';


CREATE TABLE "MYPAGE" (
	"ID"	NUMBER	DEFAULT "SPRING"."IDENTITY".nextval	NOT NULL,
	"MONEY"	FLOAT	DEFAULT 100000000.0	NOT NULL,
	"USER_BTC"	FLOAT	DEFAULT 0.0000	NOT NULL,
	"USER_ETH"	FLOAT	DEFAULT 0.0000	NOT NULL
);

COMMENT ON COLUMN "MYPAGE"."ID" IS '식별ID';

COMMENT ON COLUMN "MYPAGE"."MONEY" IS '모의투자돈';

COMMENT ON COLUMN "MYPAGE"."USER_BTC" IS '보유중인BTC';

COMMENT ON COLUMN "MYPAGE"."USER_ETH" IS '보유중인ETH';

ALTER TABLE "SITE_USER" ADD CONSTRAINT "PK_SITE_USER" PRIMARY KEY (
	"ID"
);

ALTER TABLE "COINS" ADD CONSTRAINT "PK_COINS" PRIMARY KEY (
	"COINCODE"
);

ALTER TABLE "PRICES" ADD CONSTRAINT "PK_PRICES" PRIMARY KEY (
	"PNUM"
);

ALTER TABLE "TRADE_TEST" ADD CONSTRAINT "PK_TRADE_TEST" PRIMARY KEY (
	"ID"
);

ALTER TABLE "MYPAGE" ADD CONSTRAINT "PK_MYPAGE" PRIMARY KEY (
	"ID"
);

ALTER TABLE "PRICES" ADD CONSTRAINT "FK_COINS_TO_PRICES_1" FOREIGN KEY (
	"COINCODE"
)
REFERENCES "COINS" (
	"COINCODE"
);

ALTER TABLE "TRADE_TEST" ADD CONSTRAINT "FK_SITE_USER_TO_TRADE_TEST_1" FOREIGN KEY (
	"ID"
)
REFERENCES "SITE_USER" (
	"ID"
);

ALTER TABLE "TRADE_TEST" ADD CONSTRAINT "FK_COINS_TO_TRADE_TEST_1" FOREIGN KEY (
	"COINCODE"
)
REFERENCES "COINS" (
	"COINCODE"
);

ALTER TABLE "MYPAGE" ADD CONSTRAINT "FK_SITE_USER_TO_MYPAGE_1" FOREIGN KEY (
	"ID"
)
REFERENCES "SITE_USER" (
	"ID"
);

