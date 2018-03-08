-- auto-generated definition
CREATE TABLE ISMS_DATA_CLASS
(
    CLASS_ID bigint PRIMARY KEY NOT NULL,
    CLASS_TYPE varchar(64) NOT NULL,
    PARENT_ID bigint DEFAULT 0 NOT NULL,
    CLASS_NAME varchar(64)
)

-- auto-generated definition
CREATE TABLE ISMS_DATA_CLASS_FILE
(
    CLASS_ID bigint NOT NULL,
    FILE_ID bigint NOT NULL,
    CONSTRAINT ISMS_DATA_CLASS_FILE_CLASS_ID_FILE_ID_PK PRIMARY KEY (CLASS_ID, FILE_ID)
)

INSERT INTO APP.ISMS_DATA_CLASS (CLASS_ID, CLASS_TYPE, PARENT_ID, CLASS_NAME, POSITION) VALUES (1, 'EVIDENCE', 0, 'ROOT', 0);
INSERT INTO APP.ISMS_DATA_CLASS (CLASS_ID, CLASS_TYPE, PARENT_ID, CLASS_NAME, POSITION) VALUES (2, 'DATA', 0, 'ROOT', 0);
INSERT INTO APP.ISMS_DATA_CLASS (CLASS_ID, CLASS_TYPE, PARENT_ID, CLASS_NAME, POSITION) VALUES (3, 'SECURITY', 0, 'ROOT', 0);

insert into ISMS_DATA_CLASS_FILE(CLASS_ID,FILE_ID) SELECT 1,EVIDENCE_ID from ISMS_EVIDENCE;

CREATE TABLE ISMS_DATA
(
  EVIDENCE_ID BIGINT PRIMARY KEY NOT NULL,
  DESCRIPTION LONG VARCHAR,
  PATH VARCHAR(256),
  CONTENT_TYPE VARCHAR(256),
  ARCHIVED INT DEFAULT 0,
  NAME VARCHAR(256)
);

CREATE TABLE ISMS_SECURITY
(
  EVIDENCE_ID BIGINT PRIMARY KEY NOT NULL,
  DESCRIPTION LONG VARCHAR,
  PATH VARCHAR(256),
  CONTENT_TYPE VARCHAR(256),
  ARCHIVED INT DEFAULT 0,
  NAME VARCHAR(256)
);

-- auto-generated definition
CREATE TABLE ISMS_NETWORK_EVALUATION
(
    EVALUATION_ID bigint PRIMARY KEY NOT NULL,
    EVALUATION_TARGET varchar(64),
    EVALUATION_INDEX varchar(64),
    CONTROL_ITEM varchar(255),
    RESULT varchar(255),
    CONFORMITY varchar(255),
    REMARK varchar(255),
    "ORDER" int DEFAULT 0
);

INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (1, '主机房', '物理位置的选择', 'a)机房和办公场地应选择在具有防震、防风和防雨等能力的建筑内；', '', '', '', 1);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (2, '主机房', '物理位置的选择', 'b)机房场地应避免设在建筑物的高层或地下室，以及用水设备的下层或隔壁。', '', '', '', 2);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (3, '主机房', '物理访问控制', 'a)机房出入口应安排专人值守，控制、鉴别和记录进入的人员；', '', '', '', 3);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (4, '主机房', '物理访问控制', 'b)需进入机房的外来访人员应经过申请和审批流程，并限制和监控其活动范围；', '', '', '', 4);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (5, '主机房', '物理访问控制', 'c)应对机房划分区域进行管理，区域和区域之间设置物理隔离装置，在重要区域前设置交付或安装等过渡区域；', '', '', '', 5);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (6, '主机房', '物理访问控制', 'd)重要区域应配置电子门禁系统，控制、鉴别和记录进入的人员。', '', '', '', 6);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (7, '主机房', '防盗窃和防破坏', 'a)应将主要设备放置在机房内；', '', '', '', 7);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (8, '主机房', '防盗窃和防破坏', 'b)应将设备或主要部件进行固定，并设置明显的不易除去的标记；', '', '', '', 8);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (9, '主机房', '防盗窃和防破坏', 'c)应将通信线缆铺设在隐蔽处，可铺设在地下或管道中；', '', '', '', 9);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (10, '主机房', '防盗窃和防破坏', 'd)应对介质分类标识，存储在介质库或档案室中；', '', '', '', 10);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (11, '主机房', '防盗窃和防破坏', 'e)应利用光、电等技术设置机房防盗报警系统；', '', '', '', 11);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (12, '主机房', '防盗窃和防破坏', 'f)应对机房设置监控报警系统。', '', '', '', 12);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (13, '主机房', '防雷击', 'a)机房建筑应设置避雷装置；', '', '', '', 13);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (14, '主机房', '防雷击', 'b)应设置防雷保安器，防止感应雷；', '', '', '', 14);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (15, '主机房', '防雷击', 'c)机房应设置交流电源地线。', '', '', '', 15);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (16, '主机房', '防火', 'a)机房应设置火灾自动消防系统，能够自动检测火情、自动报警，并自动灭火；', '', '', '', 16);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (17, '主机房', '防火', 'b)机房及相关的工作房间和辅助房应采用具有耐火等级的建筑材料；', '', '', '', 17);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (18, '主机房', '防火', 'c)机房应采取区域隔离防火措施，将重要设备与其他设备隔离开。', '', '', '', 18);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (19, '主机房', '防水和防潮', 'a)水管安装，不得穿过机房屋顶和活动地板下；', '', '', '', 19);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (20, '主机房', '防水和防潮', 'b)应采取措施防止雨水通过机房窗户、屋顶和墙壁渗透；', '', '', '', 20);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (21, '主机房', '防水和防潮', 'c)应采取措施防止机房内水蒸气结露和地下积水的转移与渗透；', '', '', '', 21);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (22, '主机房', '防水和防潮', 'd)应安装对水敏感的检测仪表或元件，对机房进行防水检测和报警。', '', '', '', 22);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (23, '主机房', '防静电', 'a)主要设备应采用必要的接地防静电措施；', '', '', '', 23);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (24, '主机房', '防静电', 'b)机房应采用防静电地板。', '', '', '', 24);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (25, '主机房', '温湿度控制', 'a)机房应设置温、湿度自动调节设施，使机房温、湿度的变化在设备运行所允许的范围之内。', '', '', '', 25);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (26, '主机房', '电力供应', 'a)应在机房供电线路上配置稳压器和过电压防护设备；', '', '', '', 26);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (27, '主机房', '电力供应', 'b)应提供短期的备用电力供应，至少满足主要设备在断电情况下的正常运行要求；', '', '', '', 27);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (28, '主机房', '电力供应', 'c)应设置冗余或并行的电力电缆线路为计算机系统供电；', '', '', '', 28);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (29, '主机房', '电力供应', 'd)应建立备用供电系统。', '', '', '', 29);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (30, '主机房', '电磁防护', 'a)应采用接地方式防止外界电磁干扰和设备寄生耦合干扰；', '', '', '', 30);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (31, '主机房', '电磁防护', 'b)电源线和通信线缆应隔离铺设，避免互相干扰；', '', '', '', 31);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (32, '主机房', '电磁防护', 'c)应对关键设备和磁介质实施电磁屏蔽。', '', '', '', 32);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (33, '应用安全检查', '身份鉴别', 'a)应提供专用的登录控制模块对登录用户进行身份标识和鉴别；', '', '', '', 1);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (34, '应用安全检查', '身份鉴别', 'b)应对同一用户采用两种或两种以上组合的鉴别技术实现用户身份鉴别；', '', '', '', 2);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (35, '应用安全检查', '身份鉴别', 'c)应提供用户身份标识唯一和鉴别信息复杂度检查功能，保证应用系统中不存在重复用户身份标识，身份鉴别信息不易被冒用；', '', '', '', 3);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (36, '应用安全检查', '身份鉴别', 'd)应提供登录失败处理功能，可采取结束会话、限制非法登录次数和自动退出等措施；', '', '', '', 4);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (37, '应用安全检查', '身份鉴别', 'e)应启用身份鉴别、用户身份标识唯一性检查、用户身份鉴别信息复杂度检查以及登录失败处理功能，并根据安全策略配置相关参数。', '', '', '', 5);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (38, '应用安全检查', '访问控制', 'a)应提供访问控制功能，依据安全策略控制用户对文件、数据库表等客体的访问；', '', '', '', 6);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (39, '应用安全检查', '访问控制', 'b)访问控制的覆盖范围应包括与资源访问相关的主体、客体及它们之间的操作；', '', '', '', 7);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (40, '应用安全检查', '访问控制', 'c)应由授权主体配置访问控制策略，并严格限制默认帐户的访问权限；', '', '', '', 8);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (41, '应用安全检查', '访问控制', 'd)应授予不同帐户为完成各自承担任务所需的最小权限，并在它们之间形成相互制约的关系；', '', '', '', 9);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (42, '应用安全检查', '访问控制', 'e)应具有对重要信息资源设置敏感标记的功能；', '', '', '', 10);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (43, '应用安全检查', '访问控制', 'f)应依据安全策略严格控制用户对有敏感标记重要信息资源的操作。', '', '', '', 11);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (44, '应用安全检查', '安全审计', 'a)应提供覆盖到每个用户的安全审计功能，对应用系统重要安全事件进行审计；', '', '', '', 12);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (45, '应用安全检查', '安全审计', 'b)应保证无法单独中断审计进程，无法删除、修改或覆盖审计记录；', '', '', '', 13);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (46, '应用安全检查', '安全审计', 'c)审计记录的内容至少应包括事件的日期、时间、发起者信息、类型、描述和结果等；', '', '', '', 14);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (47, '应用安全检查', '安全审计', 'd)应提供对审计记录数据进行统计、查询、分析及生成审计报表的功能。', '', '', '', 15);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (48, '应用安全检查', '剩余信息保护', 'a)应保证用户鉴别信息所在的存储空间被释放或再分配给其他用户前得到完全清除，无论这些信息是存放在硬盘上还是在内存中；', '', '', '', 16);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (49, '应用安全检查', '剩余信息保护', 'b)应保证系统内的文件、目录和数据库记录等资源所在的存储空间被释放或重新分配给其他用户前得到完全清除。', '', '', '', 17);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (50, '应用安全检查', '通信完整性', 'a)应采用密码技术保证通信过程中数据的完整性。', '', '', '', 18);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (51, '应用安全检查', '通信保密性', 'a)在通信双方建立连接之前，应用系统应利用密码技术进行会话初始化验证；', '', '', '', 19);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (52, '应用安全检查', '通信保密性', 'b)应对通信过程中的整个报文或会话过程进行加密。', '', '', '', 20);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (53, '应用安全检查', '抗抵赖', 'a)应具有在请求的情况下为数据原发者或接收者提供数据原发证据的功能；', '', '', '', 21);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (54, '应用安全检查', '抗抵赖', 'b)应具有在请求的情况下为数据原发者或接收者提供数据接收证据的功能。', '', '', '', 22);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (55, '应用安全检查', '软件容错', 'a)应提供数据有效性检验功能，保证通过人机接口输入或通过通信接口输入的数据格式或长度符合系统设定要求；', '', '', '', 23);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (56, '应用安全检查', '软件容错', 'b)应提供自动保护功能，当故障发生时自动保护当前所有状态，保证系统能够进行恢复。', '', '', '', 24);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (57, '应用安全检查', '资源控制', 'a)当应用系统的通信双方中的一方在一段时间内未作任何响应，另一方应能够自动结束会话；', '', '', '', 25);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (58, '应用安全检查', '资源控制', 'b)应能够对系统的最大并发会话连接数进行限制；', '', '', '', 26);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (59, '应用安全检查', '资源控制', 'c)应能够对单个帐户的多重并发会话进行限制；', '', '', '', 27);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (60, '应用安全检查', '资源控制', 'd)应能够对一个时间段内可能的并发会话连接数进行限制；', '', '', '', 28);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (61, '应用安全检查', '资源控制', 'e)应能够对一个访问帐户或一个请求进程占用的资源分配最大限额和最小限额；', '', '', '', 29);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (62, '应用安全检查', '资源控制', 'f)应能够对系统服务水平降低到预先规定的最小值进行检测和报警；', '', '', '', 30);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (63, '应用安全检查', '资源控制', 'g)应提供服务优先级设定功能，并在安装后根据安全策略设定访问帐户或请求进程的优先级，根据优先级分配系统资源。', '', '', '', 31);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (64, '主机安全', '身份鉴别', 'a)应对登录操作系统和数据库系统的用户进行身份标识和鉴别；', '', '', '', 1);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (65, '主机安全', '身份鉴别', 'b)操作系统和数据库系统管理用户身份标识应具有不易被冒用的特点，口令应有复杂度要求并定期更换；', '', '', '', 2);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (66, '主机安全', '身份鉴别', 'c)应启用登录失败处理功能，可采取结束会话、限制非法登录次数和自动退出等措施；', '', '', '', 3);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (67, '主机安全', '身份鉴别', 'd)当对服务器进行远程管理时，应采取必要措施，防止鉴别信息在网络传输过程中被窃听；', '', '', '', 4);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (68, '主机安全', '身份鉴别', 'e)应为操作系统和数据库系统的不同用户分配不同的用户名，确保用户名具有唯一性；', '', '', '', 5);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (69, '主机安全', '身份鉴别', 'f)应采用两种或两种以上组合的鉴别技术对管理用户进行身份鉴别。', '', '', '', 6);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (70, '主机安全', '访问控制', 'a)应启用访问控制功能，依据安全策略控制用户对资源的访问；', '', '', '', 7);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (71, '主机安全', '访问控制', 'b)应根据管理用户的角色分配权限，实现管理用户的权限分离，仅授予管理用户所需的最小权限；', '', '', '', 8);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (72, '主机安全', '访问控制', 'c)应实现操作系统和数据库系统特权用户的权限分离；', '', '', '', 9);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (73, '主机安全', '访问控制', 'd)应严格限制默认帐户的访问权限，重命名系统默认帐户，修改这些帐户的默认口令；', '', '', '', 10);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (74, '主机安全', '访问控制', 'e)应及时删除多余的、过期的帐户，避免共享帐户的存在；', '', '', '', 11);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (75, '主机安全', '访问控制', 'f)应对重要信息资源设置敏感标记；', '', '', '', 12);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (76, '主机安全', '访问控制', 'g)应依据安全策略严格控制用户对有敏感标记重要信息资源的操作。', '', '', '', 13);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (77, '主机安全', '安全审计', 'a)审计范围应覆盖到服务器和重要客户端上的每个操作系统用户和数据库用户；', '', '', '', 14);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (78, '主机安全', '安全审计', 'b)审计内容应包括重要用户行为、系统资源的异常使用和重要系统命令的使用等系统内重要的安全相关事件；', '', '', '', 15);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (79, '主机安全', '安全审计', 'c)审计记录应包括事件的日期、时间、类型、主体标识、客体标识和结果等；', '', '', '', 16);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (80, '主机安全', '安全审计', 'd)应能够根据记录数据进行分析，并生成审计报表；', '', '', '', 17);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (81, '主机安全', '安全审计', 'e)应保护审计进程，避免受到未预期的中断；', '', '', '', 18);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (82, '主机安全', '安全审计', 'f)应保护审计记录，避免受到未预期的删除、修改或覆盖等。', '', '', '', 19);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (83, '主机安全', '剩余信息保护', 'a)应保证操作系统和数据库系统用户的鉴别信息所在的存储空间，被释放或再分配给其他用户前得到完全清除，无论这些信息是存放在硬盘上还是在内存中；', '', '', '', 20);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (84, '主机安全', '剩余信息保护', 'b)应确保系统内的文件、目录和数据库记录等资源所在的存储空间，被释放或重新分配给其他用户前得到完全清除。', '', '', '', 21);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (85, '主机安全', '入侵防范', 'a)应能够检测到对重要服务器进行入侵的行为，能够记录入侵的源IP、攻击的类型、攻击的目的、攻击的时间，并在发生严重入侵事件时提供报警；', '', '', '', 22);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (86, '主机安全', '入侵防范', 'b)应能够对重要程序的完整性进行检测，并在检测到完整性受到破坏后具有恢复的措施；', '', '', '', 23);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (87, '主机安全', '入侵防范', 'c)操作系统应遵循最小安装的原则，仅安装需要的组件和应用程序，并通过设置升级服务器等方式保持系统补丁及时得到更新。', '', '', '', 24);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (88, '主机安全', '恶意代码防范', 'a)应安装防恶意代码软件，并及时更新防恶意代码软件版本和恶意代码库；', '', '', '', 25);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (89, '主机安全', '恶意代码防范', 'b)主机防恶意代码产品应具有与网络防恶意代码产品不同的恶意代码库；', '', '', '', 26);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (90, '主机安全', '恶意代码防范', 'c)应支持防恶意代码的统一管理。', '', '', '', 27);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (91, '主机安全', '资源控制', 'a)应通过设定终端接入方式、网络地址范围等条件限制终端登录；', '', '', '', 28);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (92, '主机安全', '资源控制', 'b)应根据安全策略设置登录终端的操作超时锁定；', '', '', '', 29);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (93, '主机安全', '资源控制', 'c)应对重要服务器进行监视，包括监视服务器的CPU、硬盘、内存、网络等资源的使用情况；', '', '', '', 30);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (94, '主机安全', '资源控制', 'd)应限制单个用户对系统资源的最大或最小使用限度；', '', '', '', 31);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (95, '主机安全', '资源控制', 'e)应能够对系统的服务水平降低到预先规定的最小值进行检测和报警。', '', '', '', 32);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (96, 'Mysql数据库', '身份鉴别', 'a)应对登录操作系统和数据库系统的用户进行身份标识和鉴别；', '', '', '', 1);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (97, 'Mysql数据库', '身份鉴别', 'b)操作系统和数据库系统管理用户身份标识应具有不易被冒用的特点，口令应有复杂度要求并定期更换；', '', '', '', 2);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (98, 'Mysql数据库', '身份鉴别', 'c)应启用登录失败处理功能，可采取结束会话、限制非法登录次数和自动退出等措施；', '', '', '', 3);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (99, 'Mysql数据库', '身份鉴别', 'd)当对服务器进行远程管理时，应采取必要措施，防止鉴别信息在网络传输过程中被窃听；', '', '', '', 4);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (100, 'Mysql数据库', '身份鉴别', 'e)应为操作系统和数据库系统的不同用户分配不同的用户名，确保用户名具有唯一性；', '', '', '', 5);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (101, 'Mysql数据库', '身份鉴别', 'f)应采用两种或两种以上组合的鉴别技术对管理用户进行身份鉴别。', '', '', '', 6);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (102, 'Mysql数据库', '访问控制', 'a)应启用访问控制功能，依据安全策略控制用户对资源的访问；', '', '', '', 7);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (103, 'Mysql数据库', '访问控制', 'b)应根据管理用户的角色分配权限，实现管理用户的权限分离，仅授予管理用户所需的最小权限；', '', '', '', 8);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (104, 'Mysql数据库', '访问控制', 'c)应实现操作系统和数据库系统特权用户的权限分离；', '', '', '', 9);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (105, 'Mysql数据库', '访问控制', 'd)应严格限制默认帐户的访问权限，重命名系统默认帐户，修改这些帐户的默认口令；', '', '', '', 10);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (106, 'Mysql数据库', '访问控制', 'e)应及时删除多余的、过期的帐户，避免共享帐户的存在；', '', '', '', 11);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (107, 'Mysql数据库', '访问控制', 'f)应对重要信息资源设置敏感标记；', '', '', '', 12);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (108, 'Mysql数据库', '访问控制', 'g)应依据安全策略严格控制用户对有敏感标记重要信息资源的操作。', '', '', '', 13);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (109, 'Mysql数据库', '安全审计', 'a)审计范围应覆盖到服务器和重要客户端上的每个操作系统用户和数据库用户；', '', '', '', 14);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (110, 'Mysql数据库', '安全审计', 'b)审计内容应包括重要用户行为、系统资源的异常使用和重要系统命令的使用等系统内重要的安全相关事件；', '', '', '', 15);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (111, 'Mysql数据库', '安全审计', 'c)审计记录应包括事件的日期、时间、类型、主体标识、客体标识和结果等；', '', '', '', 16);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (112, 'Mysql数据库', '安全审计', 'd)应能够根据记录数据进行分析，并生成审计报表；', '', '', '', 17);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (113, 'Mysql数据库', '安全审计', 'e)应保护审计进程，避免受到未预期的中断；', '', '', '', 18);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (114, 'Mysql数据库', '安全审计', 'f)应保护审计记录，避免受到未预期的删除、修改或覆盖等。', '', '', '', 19);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (115, 'Mysql数据库', '剩余信息保护', 'a)应保证操作系统和数据库系统用户的鉴别信息所在的存储空间，被释放或再分配给其他用户前得到完全清除，无论这些信息是存放在硬盘上还是在内存中；', '', '', '', 20);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (116, 'Mysql数据库', '剩余信息保护', 'b)应确保系统内的文件、目录和数据库记录等资源所在的存储空间，被释放或重新分配给其他用户前得到完全清除。', '', '', '', 21);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (117, 'Mysql数据库', '入侵防范', 'a)应能够检测到对重要服务器进行入侵的行为，能够记录入侵的源IP、攻击的类型、攻击的目的、攻击的时间，并在发生严重入侵事件时提供报警；', '', '', '', 22);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (118, 'Mysql数据库', '入侵防范', 'b)应能够对重要程序的完整性进行检测，并在检测到完整性受到破坏后具有恢复的措施；', '', '', '', 23);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (119, 'Mysql数据库', '入侵防范', 'c)操作系统应遵循最小安装的原则，仅安装需要的组件和应用程序，并通过设置升级服务器等方式保持系统补丁及时得到更新。', '', '', '', 24);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (120, 'Mysql数据库', '资源控制', 'a)应通过设定终端接入方式、网络地址范围等条件限制终端登录；', '', '', '', 25);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (121, 'Mysql数据库', '资源控制', 'b)应根据安全策略设置登录终端的操作超时锁定；', '', '', '', 26);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (122, 'Mysql数据库', '资源控制', 'c)应对重要服务器进行监视，包括监视服务器的CPU、硬盘、内存、网络等资源的使用情况；', '', '', '', 27);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (123, 'Mysql数据库', '资源控制', 'd)应限制单个用户对系统资源的最大或最小使用限度；', '', '', '', 28);
INSERT INTO APP.ISMS_NETWORK_EVALUATION (EVALUATION_ID, EVALUATION_TARGET, EVALUATION_INDEX, CONTROL_ITEM, RESULT, CONFORMITY, REMARK, "ORDER") VALUES (124, 'Mysql数据库', '资源控制', 'e)应能够对系统的服务水平降低到预先规定的最小值进行检测和报警。', '', '', '', 29);


CREATE TABLE APP.ISMS_DEPT
(
    DEPT_ID VARCHAR(64) PRIMARY KEY,
    DEPT_NAME VARCHAR(64)
);