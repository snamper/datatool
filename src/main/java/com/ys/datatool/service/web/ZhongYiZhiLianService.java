package com.ys.datatool.service.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ys.datatool.domain.*;
import com.ys.datatool.util.ConnectionUtil;
import com.ys.datatool.util.ExportUtil;
import com.ys.datatool.util.WebClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Response;
import org.apache.http.message.BasicNameValuePair;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Created by mo on @date  2018/4/27.
 * 中易智联
 */
@Service
public class ZhongYiZhiLianService {

    private String UPDATECARDVALIDTIME_URL = "http://boss.xmzyzl.com/Customer/MemberManage/SaveDate?";

    private String MEMBERCARDEXPIRE_URL = "http://boss.xmzyzl.com/Customer/MemberManage/GetSearchResult?limit=20&offset={offset}&StartTime=&EndTime=&CARDTYPEID=&EMPLOYEEID=&TYPE=3&shop=&SHOPID=&keyword=";

    private String MEMBERCARDDETAIL_URL = "http://boss.xmzyzl.com/Customer/MemberDetailed/Query?order=asc";

    private String MEMBERCARD_URL = "http://boss.xmzyzl.com/Customer/MemberManage/GetSearchResult?limit=20&offset={offset}&StartTime=&EndTime=&CARDTYPEID=&EMPLOYEEID=&SHOPID=&keyword=&TYPE=&shop=";

    private String MEMBERCARDITEM_URL = "http://boss.xmzyzl.com/Customer/MemberManage/PackageQuery";

    private String STOCK_URL = "http://boss.xmzyzl.com/Store/StoreSearch/Query?limit=50&WAREHOUSEID=&keywords=&cbtnZero=&treeId=&hdkeywords=&offset={offset}&SHOPID=";

    private String STOCKCOST_URL = "http://boss.xmzyzl.com/Store/StoreSearch/CostQuery";

    private String CARINFODETAIL_URL = "http://boss.xmzyzl.com/Customer/CustomerAdd/QueryIndex";

    private String CARINFO_URL = "http://boss.xmzyzl.com/Customer/CustomerCar/Query?limit=20&firstShopStartTime=&firstShopEndTime=&memberFlag=&shop=&keyword=&offset={offset}";

    private String SERVICE_URL = "http://boss.xmzyzl.com/Basic/Item/Query?limit=20&startDate=&endDate=&name=&state=&shop=&offset={offset}";

    private String ITEM_URL = "http://boss.xmzyzl.com/Basic/ProductManage/Query?limit=20&startDate=&endDate=&shop=&name=&offset={offset}";

    private String SUPPLIERDETAIL_URL = "http://boss.xmzyzl.com/Basic/Supplier/SuppQuery?SuppID={id}";

    private String SUPPLIER_URL = "http://boss.xmzyzl.com/Basic/Supplier/Query";

    private String ACCEPT = "application/json, text/javascript, */*; q=0.01";

    private String CONNECTION = "keep-alive";

    private String HOST = "boss.xmzyzl.com";

    private String ORIGIN = "http://boss.xmzyzl.com";

    private String REFERER = "http://boss.xmzyzl.com/Basic/Supplier/Index";

    private String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";

    private String X_REQUESTED_WITH = "XMLHttpRequest";

    private String UPGRADE_INSECURE_REQUESTS = "1";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String fieldName = "total";

    private int num = 20;//分页参数为10、15、20、25

    private int stockNum = 50;//分页参数为10、25、50、100

    private Workbook workbook;

    private String begintime = "2015-01-01";

    private String endtime = "2018-05-31";

    private String COOKIE = "_uab_collina=152482180507203105356301; UM_distinctid=1630669539e4d7-0a6eaa05d64647-4323461-144000-1630669539f73c; Hm_lvt_d5f79ec73fc60538d41315484cc9d7ad=1524820957; acw_tc=AQAAAPf8nnN7OwgAndgcDuabEt8/goy1; ASP.NET_SessionId=wggtkgixdmo4ycfnpdijvrj0; spellName=; u_asec=099%23KAFE07EKE3EEhGTLEEEEEpEQz0yFD6ATSu9EQ60ESrioW6NESuREV6fEDqMTEHME8RRW%2FqYWcTZB95esCR4B3R07Fps1HshnPvZ1UfIWPs2CivZBQO4WPsMRiz9Zc0X46dbROq7WcLYf9BZZZy8VHaG65pnB6gXAPGDt89%2BS6VX4iorACmGTEELStE1KXAzdtXQTEEyZtY7EZQlbE7EU1ljrlAKw2hLStTTnsyaZ7%2FiSH3lP%2F3XBt375luZdtdlSt3tusyaSWcYTEHRElIami1bRvFd63uEDRKnykg4OVBAd1PbRJxi4cBSqxbWMXyTY2bsMk6Arti5WxKNf8Z%2Bg2hWy1PbR9lAWoPD2JfgLlB5znPgMnhsDqweokjDfjYFET%2FiDlllP%2F3QTEEx5Lq5jBYFETEEEbOR5E7EFt37EFE%3D%3D";

    /**
     * 家喻汽车集团各店(12间)
     * 兴隆店 07a21bd0061747418418cb54299402fajl
     * 碧海店 1548c2f4d8854c4f9e1abffd0fc0a175
     * 中铁逸都店 206819be63c240e69cdd49cbd08c76c1
     * 世纪城店 30a5a584ba7c46248c8c5276b3731b82
     * 广场店  32d97ecd5c2c4e569e05cb15b00fff66
     * 蟠桃宫店 708ae79e2bdd48a591ad2af6c91d0a32
     * 平桥店  cb780f5eb03a48f7ba93c0c9e3ad54d4
     * 贵州家喻集团汽车服务有限公司 c4554d26854f47ab8d089aed29fd0c1f
     * 北衙路店 6956a41acd464d81aee8f9f2e534beba
     * 朝阳店  8f2a276046b74ee29bc5001988a757a2
     * 都匀店  aa2d1ea43fbf4b47ad4dc8e6a7cd95cb
     * 北新区店 d50149edd5b742a1adf9dfaf1ad1e94a
     */
    private String shopId = "c4554d26854f47ab8d089aed29fd0c1f";//车店编号

    private String companyName = "贵州家喻集团汽车服务有限公司";


    /**
     * 批量更新会员卡到期时间
     * 系统服务器每次只允许调用接口100次
     *
     * @throws IOException
     */
    @Test
    public void batchUpdateCardValidTime() throws IOException {
        List<MemberCard> memberCards = new ArrayList<>();
        Response response = ConnectionUtil.doGet(StringUtils.replace(MEMBERCARDEXPIRE_URL, "{offset}", "0"), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
        int totalPage = WebClientUtil.getTotalPage(response, MAPPER, fieldName, num);

        if (totalPage > 0) {
            int offSet = 0;

            for (int i = 1; i <= totalPage; i++) {
                response = ConnectionUtil.doGet(StringUtils.replace(MEMBERCARDEXPIRE_URL, "{offset}", String.valueOf(offSet)), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
                JsonNode result = MAPPER.readTree(response.returnContent().asString());

                offSet = offSet + num;
                System.out.println("offSet大小为" + offSet);
                System.out.println("页数为" + i);

                Iterator<JsonNode> it = result.get("rows").iterator();
                while (it.hasNext()) {
                    JsonNode element = it.next();

                    String cardCode = element.get("CARDNUMBER").asText();
                    String cuId = element.get("CUID").asText();
                    String id = element.get("ID").asText();
                    String ctId = element.get("CTID").asText();
                    String txtendtime = element.get("MATURITY").asText();
                    String txtchangetime = "2018-05-25";//延期时间

                    MemberCard memberCard = new MemberCard();
                    memberCard.setMemberCardId(id);
                    memberCard.setValidTime(txtendtime);
                    memberCard.setChangeTime(txtchangetime);
                    memberCard.setCtId(ctId);
                    memberCard.setCardCode(cardCode);
                    memberCard.setCuId(cuId);
                    memberCards.add(memberCard);
                }
            }
        }

        if (memberCards.size() > 0) {

            int index = 1;
            for (MemberCard memberCard : memberCards) {
                ++index;
                System.out.println("卡号为" + memberCard.getCardCode() + "正在延期.....");
                ConnectionUtil.doPost(UPDATECARDVALIDTIME_URL, getCardValidTimeParams(memberCard.getMemberCardId(), memberCard.getCuId(), memberCard.getValidTime(), memberCard.getChangeTime(), memberCard.getCtId()), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
                System.out.println("卡号为" + memberCard.getCardCode() + "延期成功");
                System.out.println("共延期了"+index+"张会员卡");
            }
        }

        System.out.println("过期会员卡分别为" + memberCards.toString());
        System.out.println("过期会员卡总数为" + memberCards.size());
        System.out.println("所有过期会员卡延期完成");

    }


    /**
     * 过期会员卡
     *
     * @throws IOException
     */
    @Test
    public void fetchExpireMemberCardData() throws IOException {
        List<MemberCard> memberCards = new ArrayList<>();
        Response response = ConnectionUtil.doGet(StringUtils.replace(MEMBERCARDEXPIRE_URL, "{offset}", "0"), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
        int totalPage = WebClientUtil.getTotalPage(response, MAPPER, fieldName, num);

        if (totalPage > 0) {
            int offSet = 0;

            for (int i = 1; i <= totalPage; i++) {
                response = ConnectionUtil.doGet(StringUtils.replace(MEMBERCARDEXPIRE_URL, "{offset}", String.valueOf(offSet)), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
                JsonNode result = MAPPER.readTree(response.returnContent().asString());

                offSet = offSet + num;
                System.out.println("offSet大小为" + offSet);
                System.out.println("页数为" + i);

                Iterator<JsonNode> it = result.get("rows").iterator();
                while (it.hasNext()) {
                    JsonNode element = it.next();

                    String id = element.get("ID").asText();
                    String clientId = element.get("CUSTOMERID").asText();
                    String cardCode = element.get("CARDNUMBER").asText();
                    String companyName = element.get("SHOPNAME").asText();
                    String memberCardName = element.get("CTNAME").asText();
                    String name = element.get("CUNAME").asText();
                    String phone = element.get("MOBILE").asText();
                    String carNumber = element.get("CARNUMBER").asText();
                    String dateCreated = element.get("SORTTIME").asText();//"CREATETIME"
                    String balance = element.get("CARDBALANCE").asText();
                    String validTime = element.get("MATURITY").asText();
                    String state = element.get("STATE").asText();

                    MemberCard memberCard = new MemberCard();
                    memberCard.setCompanyName(companyName);
                    memberCard.setCardCode(cardCode);
                    memberCard.setMemberCardName(memberCardName);
                    memberCard.setName(name == "null" ? "" : name);
                    memberCard.setPhone(phone == "null" ? "" : phone);
                    memberCard.setCarNumber(carNumber);
                    memberCard.setDateCreated(dateCreated);
                    memberCard.setBalance(balance);
                    memberCard.setRemark(validTime);
                    memberCard.setMemberCardId(id);
                    memberCard.setCardType(clientId);
                    memberCards.add(memberCard);
                }
            }
        }

        System.out.println("结果为" + memberCards.toString());
        System.out.println("memberCards大小为" + memberCards.size());
        System.out.println("大小为" + totalPage);

        String pathname = "D:\\过期会员卡导出.xls";
        ExportUtil.exportMemberCardDataInLocal(memberCards, workbook, pathname);
    }

    /**
     * 过期卡卡内项目(此方法获取到的数据有误)
     *
     * @throws IOException
     */
    @Test
    public void fetchExpireMemberCardItemData() throws IOException {
        List<MemberCardItem> memberCardItems = new ArrayList<>();
        // TODO: 2018/5/22 卡内项目详情页面的cookies会变，需要获取上次访问的cookie
    }

    /**
     * 卡内项目
     *
     * @throws Exception
     */
    @Test
    public void fetchMemberCardItemData() throws Exception {
        List<MemberCardItem> memberCardItems = new ArrayList<>();
        Map<String, Product> productMap = new HashMap<>();

        Response response = null;
        int totalPage = 0;
        //服务项目
        response = ConnectionUtil.doGet(StringUtils.replace(SERVICE_URL, "{offset}", "0"), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
        totalPage = WebClientUtil.getTotalPage(response, MAPPER, fieldName, num);
        if (totalPage > 0) {
            int offSet = 0;
            for (int i = 1; i <= totalPage; i++) {
                response = ConnectionUtil.doGet(StringUtils.replace(SERVICE_URL, "{offset}", String.valueOf(offSet)), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
                JsonNode result = MAPPER.readTree(response.returnContent().asString());

                offSet = offSet + num;
                Iterator<JsonNode> it = result.get("rows").iterator();
                while (it.hasNext()) {
                    JsonNode element = it.next();
                    String productName = element.get("NAME").asText();
                    String code = element.get("CODE").asText();
                    String firstCategoryName = element.get("ITEMCLASSNAME").asText();
                    String price = element.get("SELLPRICE").asText();
                    String id = element.get("ID").asText();

                    Product product = new Product();
                    product.setProductName(productName);
                    product.setCode(code);
                    product.setItemType("服务项");
                    product.setFirstCategoryName(firstCategoryName);
                    product.setPrice(price);
                    productMap.put(id, product);
                }
            }
        }

        //商品
        response = ConnectionUtil.doGet(StringUtils.replace(ITEM_URL, "{offset}", "0"), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
        totalPage = WebClientUtil.getTotalPage(response, MAPPER, fieldName, num);
        if (totalPage > 0) {
            int offSet = 0;

            for (int i = 1; i <= totalPage; i++) {
                response = ConnectionUtil.doGet(StringUtils.replace(ITEM_URL, "{offset}", String.valueOf(offSet)), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
                JsonNode result = MAPPER.readTree(response.returnContent().asString());

                offSet = offSet + num;
                Iterator<JsonNode> it = result.get("rows").iterator();
                while (it.hasNext()) {
                    JsonNode element = it.next();

                    String code = element.get("CODE").asText();
                    String productName = element.get("NAME").asText();
                    String firstCategoryName = element.get("PRODUCTCLASSNAME").asText();
                    String price = element.get("SELLPRICE").asText();
                    String barCode = element.get("BARCODE").asText();
                    String id = element.get("ID").asText();

                    Product product = new Product();
                    product.setCode(code);
                    product.setProductName(productName);
                    product.setFirstCategoryName(firstCategoryName);
                    product.setPrice(price);
                    product.setItemType("配件");
                    product.setBarCode(barCode == "null" ? "" : barCode);
                    productMap.put(id, product);
                }
            }
        }

        //会员卡内项目
        response = ConnectionUtil.doPost(MEMBERCARDITEM_URL, getMemberCardItemParams(shopId), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
        JsonNode result = MAPPER.readTree(response.returnContent().asString());

        Iterator<JsonNode> it = result.iterator();
        while (it.hasNext()) {
            JsonNode element = it.next();

            String id = element.get("PRODUCTID").asText();
            String companyName = element.get("SHOPNAME").asText();
            String cardCode = element.get("CARDNUMBER").asText();
            String itemName = element.get("PNAME").asText();
            String originalNum = element.get("SUMACOUNT").asText();
            String num = element.get("NUM").asText();
            String validTime = element.get("EXPIRYTIME").asText();
            String isValidForever = "";

            if (StringUtils.isNotBlank(validTime) || validTime != null) {
                isValidForever = "否";
            } else {
                isValidForever = "是";
            }

            Product product = productMap.get(id);
            MemberCardItem memberCardItem = new MemberCardItem();
            memberCardItem.setCompanyName(companyName);
            memberCardItem.setCardCode(cardCode);
            memberCardItem.setItemName(itemName);
            memberCardItem.setDiscount("0");
            memberCardItem.setNum(num);
            memberCardItem.setOriginalNum(originalNum);
            memberCardItem.setValidTime(validTime);
            memberCardItem.setIsValidForever(isValidForever);
            memberCardItem.setPrice(product.getPrice());
            memberCardItem.setFirstCategoryName(product.getFirstCategoryName());
            memberCardItem.setCode(product.getCode());
            memberCardItem.setItemType(product.getItemType());
            memberCardItems.add(memberCardItem);
        }

        System.out.println("结果为" + memberCardItems.toString());
        System.out.println("memberCardItems大小为" + memberCardItems.size());

        String pathname = "D:\\{companyName}卡内项目导出.xlsx";
        ExportUtil.exportMemberCardItemDataInLocal(memberCardItems, workbook, StringUtils.replace(pathname, "{companyName}", companyName));
    }

    /**
     * 会员卡
     *
     * @throws Exception
     */
    @Test
    public void fetchMemberCardData() throws Exception {
        List<MemberCard> memberCards = new ArrayList<>();
        Response response = ConnectionUtil.doGet(StringUtils.replace(MEMBERCARD_URL, "{offset}", "0") + shopId, ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
        int totalPage = WebClientUtil.getTotalPage(response, MAPPER, fieldName, num);

        if (totalPage > 0) {
            int offSet = 0;
            //因为家喻总店会员卡数据太多，要分状态抓取，修改地址MEMBERCARD_URL中TYPE=0为正常，1为挂失，3为过期
            for (int i = 1; i <= totalPage; i++) {
                response = ConnectionUtil.doGet(StringUtils.replace(MEMBERCARD_URL, "{offset}", String.valueOf(offSet)) + shopId, ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
                JsonNode result = MAPPER.readTree(response.returnContent().asString());

                offSet = offSet + num;
                System.out.println("offSet大小为" + offSet);
                System.out.println("页数为" + i);
                Iterator<JsonNode> it = result.get("rows").iterator();
                while (it.hasNext()) {
                    JsonNode element = it.next();

                    String cardCode = element.get("CARDNUMBER").asText();
                    String companyName = element.get("SHOPNAME").asText();
                    String memberCardName = element.get("CTNAME").asText();
                    String name = element.get("CUNAME").asText();
                    String phone = element.get("MOBILE").asText();
                    String carNumber = element.get("CARNUMBER").asText();
                    String dateCreated = element.get("SORTTIME").asText();//"CREATETIME"
                    String balance = element.get("CARDBALANCE").asText();
                    String state = element.get("STATE").asText();

                    MemberCard memberCard = new MemberCard();
                    memberCard.setCompanyName(companyName);
                    memberCard.setCardCode(cardCode);
                    memberCard.setMemberCardName(memberCardName);
                    memberCard.setName(name == "null" ? "" : name);
                    memberCard.setPhone(phone == "null" ? "" : phone);
                    memberCard.setCarNumber(carNumber);
                    memberCard.setDateCreated(dateCreated);
                    memberCard.setBalance(balance);
                    memberCard.setRemark(state);
                    memberCards.add(memberCard);
                }
            }
        }

        System.out.println("结果为" + memberCards.toString());
        System.out.println("memberCards大小为" + memberCards.size());
        System.out.println("大小为" + totalPage);

        String pathname = "D:\\{companyName}会员卡导出.xls";
        ExportUtil.exportMemberCardDataInLocal(memberCards, workbook, StringUtils.replace(pathname, "{companyName}", companyName));

    }

    /**
     * 库存
     *
     * @throws IOException
     */
    @Test
    public void fetchStockData() throws IOException {
        List<Stock> stocks = new ArrayList<>();
        Map<String, String> costMap = new HashMap<>();

        Response response = ConnectionUtil.doPost(STOCKCOST_URL, getStockCostParams(shopId, begintime, endtime), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
        JsonNode result = MAPPER.readTree(response.returnContent().asString());
        Iterator<JsonNode> it = result.iterator();

        while (it.hasNext()) {
            JsonNode element = it.next();

            String stockId = element.get("PRODUCTSKUID").asText();
            String cost = element.get("COSTPRICE").asText();

            if (!costMap.keySet().contains(stockId))
                costMap.put(stockId, cost);
        }

        Response res = ConnectionUtil.doGet(StringUtils.replace(STOCK_URL, "{offset}", "0"), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
        int totalPage = WebClientUtil.getTotalPage(res, MAPPER, fieldName, stockNum);

        if (totalPage > 0) {
            int offSet = 0;
            for (int i = 1; i <= totalPage; i++) {
                res = ConnectionUtil.doGet(StringUtils.replace(STOCK_URL, "{offset}", String.valueOf(offSet)) + shopId, ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
                result = MAPPER.readTree(res.returnContent().asString());

                offSet = offSet + stockNum;
                it = result.get("rows").iterator();
                while (it.hasNext()) {
                    JsonNode element = it.next();

                    String goodsName = element.get("NAME").asText();
                    //String code = element.get("CODE").asText();
                    String code = element.get("BARCODE").asText();//家喻要求条形码为商品编码
                    String companyName = element.get("SHOPNAME").asText();
                    String storeRoomName = element.get("WAREHOUSENAME").asText();
                    String num = element.get("NUM").asText();
                    String stockId = element.get("PRODUCTSKUID").asText();
                    code = code == "null" ? "" : code;

                    Stock stock = new Stock();
                    stock.setCompanyName(companyName);
                    stock.setStoreRoomName(storeRoomName);
                    stock.setGoodsName(goodsName);
                    stock.setInventoryNum(num);
                    stock.setProductCode(code);
                    stock.setPrice(costMap.get(stockId));
                    stocks.add(stock);
                }
            }
        }

        System.out.println("结果为" + stocks.toString());
        System.out.println("大小为" + stocks.size());

        String pathname = "D:\\{companyName}库存导出.xls";
        ExportUtil.exportStockDataInLocal(stocks, workbook, StringUtils.replace(pathname, "{companyName}", companyName));

    }

    /**
     * 服务项目
     *
     * @throws IOException
     */
    @Test
    public void fetchServiceData() throws IOException {
        List<Product> products = new ArrayList<>();

        Response response = ConnectionUtil.doGet(StringUtils.replace(SERVICE_URL, "{offset}", "0"), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
        int totalPage = WebClientUtil.getTotalPage(response, MAPPER, fieldName, num);

        if (totalPage > 0) {
            int offSet = 0;
            for (int i = 1; i <= totalPage; i++) {
                response = ConnectionUtil.doGet(StringUtils.replace(SERVICE_URL, "{offset}", String.valueOf(offSet)), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
                JsonNode result = MAPPER.readTree(response.returnContent().asString());

                offSet = offSet + num;
                Iterator<JsonNode> it = result.get("rows").iterator();
                while (it.hasNext()) {
                    JsonNode element = it.next();

                    String productName = element.get("NAME").asText();
                    String code = element.get("CODE").asText();
                    String firstCategoryName = element.get("ITEMCLASSNAME").asText();
                    String companyName = element.get("SHOPNAME").asText();
                    String price = element.get("SELLPRICE").asText();
                    String remark = element.get("REMARK").asText();

                    Product product = new Product();
                    product.setProductName(productName);
                    product.setCode(code);
                    product.setFirstCategoryName(firstCategoryName);
                    product.setCompanyName(companyName);
                    product.setPrice(price);
                    product.setRemark(remark == "null" ? "" : remark);
                    product.setItemType("服务项");
                    product.setIsShare("是");
                    products.add(product);
                }
            }
        }

        System.out.println("大小为" + totalPage);
        System.out.println("大小为" + products.size());
        System.out.println("结果为" + products.toString());

        String pathname = "D:\\中易智联服务项目导出.xls";
        ExportUtil.exportProductDataInLocal(products, workbook, pathname);
    }

    /**
     * 商品
     *
     * @throws IOException
     */
    @Test
    public void fetchItemData() throws IOException {
        List<Product> products = new ArrayList<>();

        Response response = ConnectionUtil.doGet(StringUtils.replace(ITEM_URL, "{offset}", "0"), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
        int totalPage = WebClientUtil.getTotalPage(response, MAPPER, fieldName, num);

        if (totalPage > 0) {
            int offSet = 0;
            for (int i = 1; i <= totalPage; i++) {
                response = ConnectionUtil.doGet(StringUtils.replace(ITEM_URL, "{offset}", String.valueOf(offSet)), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
                JsonNode result = MAPPER.readTree(response.returnContent().asString());

                offSet = offSet + num;
                Iterator<JsonNode> it = result.get("rows").iterator();
                while (it.hasNext()) {
                    JsonNode element = it.next();

                    String code = element.get("CODE").asText();
                    String productName = element.get("NAME").asText();
                    String firstCategoryName = element.get("PRODUCTCLASSNAME").asText();
                    String brandName = element.get("PRODUCTBRANDNAME").asText();
                    String unit = element.get("PRODUCTUNITNAME").asText();
                    String price = element.get("SELLPRICE").asText();
                    String companyName = element.get("SHOPNAME").asText();
                    String barCode = element.get("BARCODE").asText();
                    String remark = element.get("REMARK").asText();

                    Product product = new Product();
                    product.setCode(code);
                    product.setProductName(productName);
                    product.setFirstCategoryName(firstCategoryName);
                    product.setBrandName(brandName == "null" ? "" : brandName);
                    product.setUnit(unit == "null" ? "" : unit);
                    product.setPrice(price);
                    product.setCompanyName(companyName);
                    product.setBarCode(barCode == "null" ? "" : barCode);
                    product.setItemType("配件");
                    product.setIsShare("是");
                    product.setRemark(remark == "null" ? "" : remark);
                    products.add(product);
                }
            }
        }

        System.out.println("大小为" + products.size());
        System.out.println("结果为" + products.toString());

        String pathname = "D:\\中易智联商品导出.xls";
        ExportUtil.exportProductDataInLocal(products, workbook, pathname);

    }

    /**
     * 供应商
     *
     * @throws IOException
     */
    @Test
    public void fetchSupplierData() throws IOException {
        List<Supplier> suppliers = new ArrayList<>();
        Map<String, Supplier> supplierMap = new HashMap<>();

        Response response = ConnectionUtil.doPost(SUPPLIER_URL, getSupplierParams(String.valueOf(num), "0"), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
        int totalPage = WebClientUtil.getTotalPage(response, MAPPER, fieldName, num);

        if (totalPage > 0) {
            int offSet = 0;
            for (int i = 1; i <= totalPage; i++) {
                response = ConnectionUtil.doPost(SUPPLIER_URL, getSupplierParams(String.valueOf(num), String.valueOf(offSet)), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
                JsonNode result = MAPPER.readTree(response.returnContent().asString());

                offSet = offSet + num;
                Iterator<JsonNode> it = result.get("rows").iterator();
                while (it.hasNext()) {
                    JsonNode element = it.next();
                    String supplierId = element.get("ID").asText();
                    String code = element.get("CODE").asText();
                    String className = element.get("CLASSNAME").asText();
                    String name = element.get("NAME").asText();
                    String remark = element.get("REMARK").asText();
                    String companyName = element.get("SHOPNAME").asText();

                    String realRemark = remark == "null" ? "" : remark;
                    Supplier supplier = new Supplier();
                    supplier.setCompanyName(companyName);
                    supplier.setCode(code);
                    supplier.setName(name);
                    supplier.setRemark(className + " " + realRemark);
                    supplierMap.put(supplierId, supplier);
                }
            }
        }

        if (supplierMap.size() > 0) {
            for (String id : supplierMap.keySet()) {
                response = ConnectionUtil.doGet(StringUtils.replace(SUPPLIERDETAIL_URL, "{id}", id), ACCEPT, COOKIE, CONNECTION, HOST, REFERER, X_REQUESTED_WITH, UPGRADE_INSECURE_REQUESTS, USER_AGENT);
                JsonNode result = MAPPER.readTree(response.returnContent().asString());
                Iterator<JsonNode> it = result.iterator();
                while (it.hasNext()) {
                    JsonNode element = it.next();

                    String fax = element.get("FAX").asText();
                    String address = element.get("ADDRESS").asText();
                    String phone = element.get("PHONE").asText();
                    String mobile = element.get("MOBILE").asText();
                    String contact = element.get("CONTACT").asText();

                    Supplier s = supplierMap.get(id);
                    Supplier supplier = new Supplier();
                    supplier.setCompanyName(s.getCompanyName());
                    supplier.setCode(s.getCode());
                    supplier.setName(s.getName());
                    supplier.setRemark(s.getRemark());
                    supplier.setContactName(contact == "null" ? "" : contact);
                    supplier.setAddress(address == "null" ? "" : address);
                    supplier.setFax(fax == "null" ? "" : fax);
                    supplier.setContactPhone(mobile == "null" ? "" : mobile);
                    suppliers.add(supplier);
                }
            }
        }


        System.out.println("大小为" + supplierMap.size());
        System.out.println("结果为" + supplierMap.toString());
        System.out.println("大小为" + suppliers.size());
        System.out.println("结果为" + suppliers.toString());

        String pathname = "D:\\中易智联供应商导出.xls";
        ExportUtil.exportSupplierDataInLocal(suppliers, workbook, pathname);
    }

    /**
     * 车辆信息
     *
     * @throws IOException
     */
    @Test
    public void fetchCarInfoData() throws IOException {
        List<CarInfo> carInfos = new ArrayList<>();
        Map<String, CarInfo> carInfoMap = new HashMap<>();

        Response response = ConnectionUtil.doGet(StringUtils.replace(CARINFO_URL, "{offset}", "0"), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
        int totalPage = WebClientUtil.getTotalPage(response, MAPPER, fieldName, num);

        if (totalPage > 0) {
            int offSet = 0;
            for (int i = 1; i <= totalPage; i++) {
                response = ConnectionUtil.doGet(StringUtils.replace(CARINFO_URL, "{offset}", String.valueOf(offSet)), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);
                System.out.println("页数为" + i);
                JsonNode result = MAPPER.readTree(response.returnContent().asString());

                offSet = offSet + num;
                Iterator<JsonNode> it = result.get("rows").iterator();

                while (it.hasNext()) {
                    JsonNode element = it.next();
                    String carId = element.get("ID").asText();
                    String carNumber = element.get("CARNUMBER").asText();
                    String name = element.get("CNAME").asText();
                    String phone = element.get("MOBILE").asText();
                    String companyName = element.get("SHOPNAME").asText();
                    String brand = element.get("BRANDNAME").asText();
                    String carModel = element.get("CARTYPENAME").asText();
                    String remark = element.get("REMARK").asText();

                    CarInfo carInfo = new CarInfo();
                    carInfo.setCarNumber(carNumber);
                    carInfo.setName(name);
                    carInfo.setPhone(phone);
                    carInfo.setCompanyName(companyName);
                    carInfo.setBrand(brand);
                    carInfo.setCarModel(carModel);
                    carInfo.setRemark(remark);
                    carInfoMap.put(carId, carInfo);
                }
            }
        }

        if (carInfoMap.size() > 0) {
            for (String id : carInfoMap.keySet()) {
                response = ConnectionUtil.doPost(CARINFODETAIL_URL, getCarInfoDetailParams(id), ACCEPT, COOKIE, CONNECTION, HOST, ORIGIN, REFERER, USER_AGENT, X_REQUESTED_WITH);

                JsonNode result = MAPPER.readTree(response.returnContent().asString());
                Iterator<JsonNode> it = result.iterator();
                while (it.hasNext()) {
                    JsonNode element = it.next();
                    String VINcode = element.get("VINNUMBER").asText();
                    String engineNumber = element.get("ENGINENUMBER").asText();
                    String vcInsuranceCompany = element.get("INSURERCOMPANY").asText();
                    String tcInsuranceValidDate = element.get("COMPULSORYTIME").asText();
                    String vcInsuranceValidDate = element.get("INSURANCETIME").asText();

                    CarInfo carInfo = carInfoMap.get(id);
                    carInfo.setVINcode(VINcode);
                    carInfo.setEngineNumber(engineNumber);
                    carInfo.setVcInsuranceCompany(vcInsuranceCompany);
                    carInfo.setVcInsuranceValidDate(vcInsuranceValidDate);
                    carInfo.setTcInsuranceValidDate(tcInsuranceValidDate);
                    carInfos.add(carInfo);
                }
            }
        }

        System.out.println("大小为" + totalPage);
        System.out.println("大小为" + carInfoMap.size());
        System.out.println("结果为" + carInfoMap.toString());

        String pathname = "D:\\中易智联车辆信息导出.xlsx";
        ExportUtil.exportCarInfoDataInLocal(carInfos, workbook, pathname);
    }

    private String getMemberCardExpireReferer(String cardId, String customerId) {
        String referer = "http://boss.xmzyzl.com/Customer/MemberDetailed/Index?";
        String expireRerer = referer + "CardId=" + cardId + "&CUSTOMERID=" + customerId;

        return expireRerer;
    }

    private List<BasicNameValuePair> getCardValidTimeParams(String id, String cuId, String txtendtime, String txtchangetime, String ctId) {
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("ShopId", ""));
        params.add(new BasicNameValuePair("ID", id));
        params.add(new BasicNameValuePair("cuId", cuId));
        params.add(new BasicNameValuePair("txtendtime", txtendtime));
        params.add(new BasicNameValuePair("txtchangetime", txtchangetime));
        params.add(new BasicNameValuePair("ctId", ctId));
        params.add(new BasicNameValuePair("delayRemark", ""));
        return params;
    }

    private List<BasicNameValuePair> getCarInfoDetailParams(String carId) {
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("carId", carId));
        params.add(new BasicNameValuePair("hideMobile", ""));
        return params;
    }

    private List<BasicNameValuePair> getStockCostParams(String shopId, String begintime, String endtime) {
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("WAREHOUSEID", ""));
        params.add(new BasicNameValuePair("keywords", ""));
        params.add(new BasicNameValuePair("SHOPID", shopId));
        params.add(new BasicNameValuePair("treeId", ""));
        params.add(new BasicNameValuePair("begintime", begintime));
        params.add(new BasicNameValuePair("endtime", endtime));
        params.add(new BasicNameValuePair("billtype", ""));
        return params;
    }

    private List<BasicNameValuePair> getSupplierParams(String num, String offset) {
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("limit", num));
        params.add(new BasicNameValuePair("offset", offset));
        params.add(new BasicNameValuePair("shop", ""));
        params.add(new BasicNameValuePair("name", ""));
        params.add(new BasicNameValuePair("state", ""));
        return params;
    }

    private List<BasicNameValuePair> getMemberCardItemParams(String shopId) {
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("keyword", ""));
        params.add(new BasicNameValuePair("item", ""));
        params.add(new BasicNameValuePair("StartTime", ""));
        params.add(new BasicNameValuePair("EndTime", ""));
        params.add(new BasicNameValuePair("CardType", ""));
        params.add(new BasicNameValuePair("Type", ""));
        params.add(new BasicNameValuePair("shop", shopId));
        return params;
    }

}
