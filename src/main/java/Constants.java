import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Constants {
    //настройки для прокси-сервера
//    public static final String PROXY_HOST = "svetlana.ltespace.com";
//    public static final int PROXY_PORT = 16565;
//    public static final String LOGIN = "4soy9fg6";
//    public static final String PASSWORD = "boxhyr84";

//    public static final String PROXY_HOST = "marina.ltespace.com";
//    public static final int PROXY_PORT = 12426;
//    public static final String LOGIN = "6uxxte5v";
//    public static final String PASSWORD = "x1k7dfkc";

    public static final String PROXY_HOST = "elena.ltespace.com";
    public static final int PROXY_PORT = 19211;
    public static final String LOGIN = "f8asf6bz";
    public static final String PASSWORD = "a4f17r9f";

    public static final String URL_FOR_SWITCH_IP = "https://ltespace.com/api/private/touch?port=" + PROXY_PORT + "&login=" + LOGIN + "&password=" + PASSWORD;
                                                  //https://ltespace.com/api/private/touch?port=18487&login=pttgua83&password=xr791oom

    //marketplaces
    public static final String WILDBERIES = "Wildberies";
    public static final String OZON = "Ozon";
    public static final String MARKETPLACE_WILDBERRIES_RU = "https://www.wildberries.ru";
    public static final String MARKETPLACE_OZON_RU = "https://www.ozon.ru";

    //уровень понижения цены
    public static final String RUB = "₽";
    public static final String PERCENT = "%";

    //заказчик
    public static final String MY_SELLER = "Продавец ООО «АКСЕСС»";
    public static final String MY_SELLER_2 = "ООО «АКСЕСС»";

    public static final String BLOCKING = "блокировка сервером";

    public static final String NOT_FOUND_PAGE = "Превышено время ожидания ответа сервера либо не найдена страница товара";
    public static final String NOT_FOUND_HTML_ITEM = "html item not found";

    //статика для проверки заговков столбцов файла отчёта Wildberies
    public static final String BRAND_NAME_IN_FILE_WILDBERIES = "Бренд";//0-й столбец
    public static final String CATEGORY_NAME_IN_FILE_WILDBERIES = "Предмет";//1-й столбец
    public static final String CODE_1C_IN_FILE_WILDBERIES = "Артикул поставщика";//3-й столбец
    public static final String VENDOR_CODE_IN_FILE_WILDBERIES = "Номенклатура (код 1С)";//4-й столбец
    public static final String PRICE_U_IN_FILE_WILDBERIES = "Текущая розн. цена (до скидки)";//11-й столбец
    public static final String BASIC_SALE_IN_FILE_WILDBERIES = "Текущая скидка на сайте, %";//13-й столбец
    public static final String PROMO_SALE_IN_FILE_WILDBERIES = "Текущая скидка по промокоду, %";//16-й столбец

    //статика для проверки заговков столбцов файла отчёта 1С
    public static final String CODE_1C = "код 1с";//1-й столбец
    public static final String NOMENCLATURE_1C = "номенклатура";//2-й столбец
    public static final String SPEC_PRICE_1C = "спец-цена";//4-й столбец
    public static final String COMMISSION_1C_FOR_WILD = "комиссия";//6-й столбец
    public static final String DELIVERY_1C_FOR_WILD = "Доставка";//4-й столбец

    //статика для проверки заговков столбцов файла отчёта Ozon
    public static final String VENDOR_CODE_1C_IN_FILE_OZON = "Артикул";//0-й столбец
    public static final String VENDOR_CODE_IN_FILE_OZON = "Ozon SKU ID";//1-й столбец
    public static final String BRAND_AND_PRODUCT_NAME_IN_FILE_OZON = "Название";//2-й столбец
    public static final String PRICE_U_IN_FILE_OZON = "Цена до скидки, руб.";//16-й столбец
    public static final String BASIC_PRICE_IN_FILE_OZON = "Текущая цена (со скидкой), руб.";//17-й столбец
    public static final String PROMO_PRICE_IN_FILE_OZON = "Цена с учетом акции, руб.";//20-й столбец
    public static final String PREMIUM_PRICE_IN_FILE_OZON = "Цена с Ozon Premium, руб.";//23-й столбец


//Статические элементы, необходимые для Wildberies
    public static final String PARAM_1_1 = "Количество предметов в упаковке";
    public static final String PARAM_1_2 = "Модель";
    //Html элементы страницы моего товара
    public static final String ELEMENT_WITH_SPEC_ACTION_MY_PRODUCT = "div[class=i-spec-action-v1 ]";
    public static final String ELEMENT_WITH_PHOTO_MY_PRODUCT = "img[class=preview-photo j-zoom-preview]";
    public static final String ELEMENT_WITH_PARAMS_MY_PRODUCT = "div[class=params]";
    public static final String ELEMENT_WITH_DESCRIPTION_MY_PRODUCT = "div[class=j-description collapsable-content description-text]";
    //Html элементы страницы поискового запроса аналогов
    public static final String ELEMENT_WITH_CATALOG = "div[class=catalog_main_table j-products-container]";
    public static final String ELEMENT_WITH_EMPTY_RESULT_SEARCH_1 = "h1[class=searching-results-title]";
    public static final String ELEMENT_WITH_EMPTY_RESULT_SEARCH_2 = "p[class=searching-results-text]";
    public static final String ELEMENT_WITH_RESULT_SEARCH = "div[class=searching-results-inner]";
    //public static final String ELEMENT_WITH_PRODUCT = "div[class=dtList i-dtList j-card-item]"; элементы перестали находиться 14.04.21
    public static final String ELEMENT_WITH_PRODUCT = "div[class=dtList-inner]";
    public static final String ELEMENT_WITH_PP = "div[class=pp]";
    public static final String ELEMENT_SPAN_IN_PP = "span";
    public static final String ATTRIBUTE_WITH_VENDOR_CODE = "data-popup-nm-id";
    public static final String ELEMENT_WITH_CARD_PRODUCT = "a[class=ref_goods_n_p j-open-full-product-card]";
    public static final String ELEMENT_WITH_NAME_PRODUCT = "div[class=dtlist-inner-brand-name]";
    public static final String ATTRIBUTE_WITH_REF_FOR_PAGE_PRODUCT = "href";
    public static final String ELEMENT_WITH_REF_FOR_IMAGE = "img[class=thumbnail]";
    public static final String ATTRIBUTE_WITH_REF_FOR_IMAGE_1 = "src";
    public static final String ATTRIBUTE_WITH_REF_FOR_IMAGE_2 = "data-original";
    //public static final String ELEMENT_WITH_SPEC_ACTION = "span[class=spec-actions-catalog i-spec-action]";
    public static final String ELEMENT_WITH_SPEC_ACTION = "a[class=spec-actions-link]";//03.03.21
    public static final String ELEMENT_WITH_BRAND_NAME = "strong[class=brand-name c-text-sm]";

    //for json query
    public static final String URL_FOR_JSON_FOR_WB = "https://wbxcatalog-ru.wildberries.ru/nm-2-card/catalog?spp=0&regions=69,58,64,40,48,70,1,38,4,30,22,66&" +
            "stores=119261,121631,1193,119400,116433,117501,507,3158,120762,1733,117986,686,117413,119781&" +
            "couponsGeo=2,12,6,9&pricemarginCoeff=1.0&reg=0&appType=1&offlineBonus=0&onlineBonus=0&emp=0&locale=ru&lang=ru&nm=";


    public static final ArrayList<String> listForCable = new ArrayList<>();

    public static final ArrayList<String> listForCharging = new ArrayList<>();

    public static final ArrayList<String> listForBugs = new ArrayList<>();
    public static final ArrayList<String> listForHeadset = new ArrayList<>();
    public static final ArrayList<String> listForBrands = new ArrayList<>();
    public static final ArrayList<String> listForCategoryBy_1C = new ArrayList<>();
    public static final ArrayList<String> listForCableAllLength = new ArrayList<>();
    public static final ArrayList<String> listForAllTypeConnect = new ArrayList<>();
    public static final ArrayList<String> listForCable_Type_C_to_Type_C = new ArrayList<>();
    public static final ArrayList<String> listForCable_Type_C_to_Apple = new ArrayList<>();
    public static final ArrayList<String> listForSeriesCover = new ArrayList<>();
    public static final ArrayList<String> listForCable_AUX = new ArrayList<>();

    //for ver. 2.0/additionalParameter//////////////////////////////////////////////////////////////////////////////
    //для зарядок и кабелей
    public static final ArrayList<String> listForConnector_Type_C_to_Apple_8pin = new ArrayList<>();
    public static final ArrayList<String> listForConnector_Type_C_to_Type_C = new ArrayList<>();
    public static final ArrayList<String> listForConnector_Apple_8PIN = new ArrayList<>();
    public static final ArrayList<String> listForConnector_Micro_USB = new ArrayList<>();
    public static final ArrayList<String> listForConnector_Type_C = new ArrayList<>();
    public static final ArrayList<String> listForConnector_4in1 = new ArrayList<>();
    public static final ArrayList<String> listForConnector_3in1 = new ArrayList<>();
    public static final ArrayList<String> listForConnector_2in1 = new ArrayList<>();

    public static final ArrayList<String> listForCable_0_2m = new ArrayList<>();
    public static final ArrayList<String> listForCable_0_25m = new ArrayList<>();
    public static final ArrayList<String> listForCable_1m = new ArrayList<>();
    public static final ArrayList<String> listForCable_1_2m = new ArrayList<>();
    public static final ArrayList<String> listForCable_1_4m = new ArrayList<>();
    public static final ArrayList<String> listForCable_1_6m = new ArrayList<>();
    public static final ArrayList<String> listForCable_1_8m = new ArrayList<>();
    public static final ArrayList<String> listForCable_2m = new ArrayList<>();
    public static final ArrayList<String> listForCable_3m = new ArrayList<>();
    static {
        listForConnector_Apple_8PIN.add("apple 8 pin");
        listForConnector_Apple_8PIN.add("apple");
        listForConnector_Apple_8PIN.add("8 pin");
        listForConnector_Apple_8PIN.add("lightning");
        listForConnector_Apple_8PIN.add("iphone");
        listForConnector_Apple_8PIN.add("для айфона");

        listForConnector_Micro_USB.add("микро");
        listForConnector_Micro_USB.add("micro");
        listForConnector_Micro_USB.add("микроusb");
        listForConnector_Micro_USB.add("microusb");
        listForConnector_Micro_USB.add("микро usb");
        listForConnector_Micro_USB.add("micro usb");

        listForConnector_Type_C.add("type-c");

        listForConnector_4in1.add("4 в 1");
        listForConnector_4in1.add("4в1");
        listForConnector_4in1.add("4 in 1");
        listForConnector_4in1.add("4-in-1");
        listForConnector_4in1.add("4in1");

        listForConnector_3in1.add("3 в 1");
        listForConnector_3in1.add("3в1");
        listForConnector_3in1.add("3-в-1");
        listForConnector_3in1.add("3 in 1");
        listForConnector_3in1.add("3-in-1");
        listForConnector_3in1.add("3in1");
        listForConnector_3in1.add("lightning+micro+type-c");

        listForConnector_2in1.add("2 в 1");
        listForConnector_2in1.add("2в1");
        listForConnector_2in1.add("2 in 1");
        listForConnector_2in1.add("2-in-1");
        listForConnector_2in1.add("2-in-");
        listForConnector_2in1.add("2in1");

        listForConnector_Type_C_to_Apple_8pin.add("type-c - apple 8 pin");
        listForConnector_Type_C_to_Apple_8pin.add("type-c to apple 8 pin");
        listForConnector_Type_C_to_Apple_8pin.add("type-c to apple");
        listForConnector_Type_C_to_Apple_8pin.add("type-c - apple");
        listForConnector_Type_C_to_Apple_8pin.add("type-c - lightning");
        listForConnector_Type_C_to_Apple_8pin.add("type-c to lightning");
        listForConnector_Type_C_to_Apple_8pin.add("type-c - 8 pin");
        listForConnector_Type_C_to_Apple_8pin.add("type-c to 8 pin");
        listForConnector_Type_C_to_Apple_8pin.add("apple 8 pin - type-c");
        listForConnector_Type_C_to_Apple_8pin.add("apple 8 pin to type-c");
        listForConnector_Type_C_to_Apple_8pin.add("apple - type-c");
        listForConnector_Type_C_to_Apple_8pin.add("apple to type-c");
        listForConnector_Type_C_to_Apple_8pin.add("lightning - type-c");
        listForConnector_Type_C_to_Apple_8pin.add("lightning to type-c");

        listForConnector_Type_C_to_Type_C.add("type-c - type-c");
        listForConnector_Type_C_to_Type_C.add("type-c to type-c");

        listForCable_0_2m.add("20 см");
        listForCable_0_2m.add("0.2м");
        listForCable_0_2m.add("0.2 м");
        listForCable_0_2m.add("0.2m");
        listForCable_0_2m.add("0.2 m");
        listForCable_0_2m.add("0,2м");
        listForCable_0_2m.add("0,2 м");
        listForCable_0_2m.add("0,2m");
        listForCable_0_2m.add("0,2 m");

        listForCable_0_25m.add("25 см");
        listForCable_0_25m.add("0.25м");
        listForCable_0_25m.add("0.25 м");
        listForCable_0_25m.add("0.25m");
        listForCable_0_25m.add("0.25 m");
        listForCable_0_25m.add("0,25м");
        listForCable_0_25m.add("0,25 м");
        listForCable_0_25m.add("0,25m");
        listForCable_0_25m.add("0,25 m");

        listForCable_1m.add("100 см");
        listForCable_1m.add("1м");
        listForCable_1m.add("1 м");
        listForCable_1m.add("1m");
        listForCable_1m.add("1 m");
        listForCable_1m.add("1.0м");
        listForCable_1m.add("1.0 м");
        listForCable_1m.add("1.0m");
        listForCable_1m.add("1.0 m");
        listForCable_1m.add("1,0м");
        listForCable_1m.add("1,0 м");
        listForCable_1m.add("1,0m");
        listForCable_1m.add("1,0 m");

        listForCable_1_2m.add("120 см");
        listForCable_1_2m.add("1.2м");
        listForCable_1_2m.add("1.2 м");
        listForCable_1_2m.add("1.2m");
        listForCable_1_2m.add("1.2 m");
        listForCable_1_2m.add("1,2м");
        listForCable_1_2m.add("1,2 м");
        listForCable_1_2m.add("1,2m");
        listForCable_1_2m.add("1,2 m");
        listForCable_1_2m.add("1.2");
        listForCable_1_2m.add("1,2");

        listForCable_1_4m.add("140 см");
        listForCable_1_4m.add("1.4м");
        listForCable_1_4m.add("1.4 м");
        listForCable_1_4m.add("1.4m");
        listForCable_1_4m.add("1.4 m");
        listForCable_1_4m.add("1,4м");
        listForCable_1_4m.add("1,4 м");
        listForCable_1_4m.add("1,4m");
        listForCable_1_4m.add("1,4 m");

        listForCable_1_6m.add("160 см");
        listForCable_1_6m.add("1.6м");
        listForCable_1_6m.add("1.6 м");
        listForCable_1_6m.add("1.6m");
        listForCable_1_6m.add("1.6 m");
        listForCable_1_6m.add("1,6м");
        listForCable_1_6m.add("1,6 м");
        listForCable_1_6m.add("1,6m");
        listForCable_1_6m.add("1,6 m");

        listForCable_1_8m.add("180 см");
        listForCable_1_8m.add("1.8м");
        listForCable_1_8m.add("1.8 м");
        listForCable_1_8m.add("1.8m");
        listForCable_1_8m.add("1.8 m");
        listForCable_1_8m.add("1,8м");
        listForCable_1_8m.add("1,8 м");
        listForCable_1_8m.add("1,8m");
        listForCable_1_8m.add("1,8 m");

        listForCable_2m.add("200 см");
        listForCable_2m.add("2м");
        listForCable_2m.add("2 м");
        listForCable_2m.add("2m");
        listForCable_2m.add("2 m");
        listForCable_2m.add("2.0м");
        listForCable_2m.add("2.0 м");
        listForCable_2m.add("2.0m");
        listForCable_2m.add("2.0 m");
        listForCable_2m.add("2,0м");
        listForCable_2m.add("2,0 м");
        listForCable_2m.add("2,0m");
        listForCable_2m.add("2,0 m");

        listForCable_3m.add("300 см");
        listForCable_3m.add("3м");
        listForCable_3m.add("3 м");
        listForCable_3m.add("3m");
        listForCable_3m.add("3 m");
        listForCable_3m.add("3.0м");
        listForCable_3m.add("3.0 м");
        listForCable_3m.add("3.0m");
        listForCable_3m.add("3.0 m");
        listForCable_3m.add("3,0м");
        listForCable_3m.add("3,0 м");
        listForCable_3m.add("3,0m");
        listForCable_3m.add("3,0 m");
    }

    static boolean checkTitleDescriptionAndParamsForConnectorType(String description) {

        description = replaceNoValidConcurrence(description);

        boolean isCheck = false;

        for (String connType: listForConnector_Type_C_to_Apple_8pin){
            if (description.toLowerCase().contains(connType)){
                isCheck = true;
                break;
            }
        }
        for (String connType: listForConnector_Type_C_to_Type_C){
            if (isCheck){
                break;
            }
            if (description.toLowerCase().contains(connType)){
                isCheck = true;
                break;
            }
        }

        for (String connType: listForConnector_Apple_8PIN){
            if (isCheck){
                break;
            }
            if (description.toLowerCase().contains(connType)){
                isCheck = true;
                break;
            }
        }
        for (String connType: listForConnector_Micro_USB){
            if (isCheck){
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType: listForConnector_Type_C){
            if (isCheck){
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType: listForConnector_4in1){
            if (isCheck){
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType: listForConnector_3in1){
            if (isCheck){
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType: listForConnector_2in1){
            if (isCheck){
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        return isCheck;
    }

    static boolean checkTitleDescriptionAndParamsForLengthType(String description) {
        boolean isCheck = false;

        for (String connType: listForCable_0_2m){
            if (description.toLowerCase().contains(connType)){
                isCheck = true;
                break;
            }
        }
        for (String connType: listForCable_0_25m){
            if (isCheck){
                break;
            }
            if (description.toLowerCase().contains(connType)){
                isCheck = true;
                break;
            }
        }
        for (String connType: listForCable_1m){
            if (isCheck){
                break;
            }
            if (description.toLowerCase().contains(connType)){
                isCheck = true;
                break;
            }
        }
        for (String connType: listForCable_1_2m){
            if (isCheck){
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType: listForCable_1_4m){
            if (isCheck){
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType: listForCable_1_6m){
            if (isCheck){
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType: listForCable_1_8m){
            if (isCheck){
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType: listForCable_2m){
            if (isCheck){
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType: listForCable_3m){
            if (isCheck){
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        return isCheck;
    }

    //для защитных стекол
    public static final ArrayList<String> listForTypeProtectiveGlass_eye_protection = new ArrayList<>();
    public static final ArrayList<String> listForTypeProtectiveGlass_5d = new ArrayList<>();
    public static final ArrayList<String> listForTypeProtectiveGlass_2_5d = new ArrayList<>();
    public static final ArrayList<String> listForTypeProtectiveGlass_11d = new ArrayList<>();
    public static final ArrayList<String> listForTypeProtectiveGlass_superd = new ArrayList<>();
    public static final ArrayList<String> listForTypeProtectiveGlass_privacy = new ArrayList<>();
    static {
        listForTypeProtectiveGlass_eye_protection.add("eye protection");
        listForTypeProtectiveGlass_eye_protection.add("eye");
        listForTypeProtectiveGlass_5d.add(" 5d");
        listForTypeProtectiveGlass_11d.add(" 11d");
        listForTypeProtectiveGlass_2_5d.add(" 2.5d");
        listForTypeProtectiveGlass_superd.add("superd");
        listForTypeProtectiveGlass_superd.add("super d");
        listForTypeProtectiveGlass_superd.add("super-d");
        listForTypeProtectiveGlass_privacy.add("privacy");
    }
    static boolean checkTitleDescriptionAndParamsForTypeProtectiveGlass(String description) {
        boolean isCheck = false;

        for (String connType : listForTypeProtectiveGlass_eye_protection) {
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType : listForTypeProtectiveGlass_5d) {
            if (isCheck) {
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType : listForTypeProtectiveGlass_2_5d) {
            if (isCheck) {
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType : listForTypeProtectiveGlass_11d) {
            if (isCheck) {
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType : listForTypeProtectiveGlass_superd) {
            if (isCheck) {
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType : listForTypeProtectiveGlass_privacy) {
            if (isCheck) {
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        return isCheck;
    }

    //для чехлов
    public static final ArrayList<String> listForTypeSeriesCover_Fellwell_series = new ArrayList<>();
    public static final ArrayList<String> listForTypeSeriesCover_Pure_series = new ArrayList<>();
    public static final ArrayList<String> listForTypeSeriesCover_Silicon_case = new ArrayList<>();
    public static final ArrayList<String> listForTypeSeriesCover_Delicate_shadow = new ArrayList<>();
    public static final ArrayList<String> listForTypeSeriesCover_Fascination_series = new ArrayList<>();
    public static final ArrayList<String> listForTypeSeriesCover_Gentle_series = new ArrayList<>();
    public static final ArrayList<String> listForTypeSeriesCover_Minni_series = new ArrayList<>();
    public static final ArrayList<String> listForTypeSeriesCover_Kingdom_series = new ArrayList<>();
    public static final ArrayList<String> listForTypeSeriesCover_Light_series = new ArrayList<>();
    public static final ArrayList<String> listForTypeSeriesCover_Thin_series = new ArrayList<>();
    static{
        listForTypeSeriesCover_Fellwell_series.add("fellwell series");
        listForTypeSeriesCover_Pure_series.add("pure series");
        listForTypeSeriesCover_Silicon_case.add("silicon case");
        listForTypeSeriesCover_Delicate_shadow.add("delicate shadow");
        listForTypeSeriesCover_Fascination_series.add("fascination series");
        listForTypeSeriesCover_Gentle_series.add("gentle series");
        listForTypeSeriesCover_Minni_series.add("minni series");
        listForTypeSeriesCover_Kingdom_series.add("kingdom series");
        listForTypeSeriesCover_Light_series.add("light series");
        listForTypeSeriesCover_Thin_series.add("thin series");
    }
    static boolean checkTitleDescriptionAndParamsForTypeTypeSeriesCover(String description) {
        boolean isCheck = false;

        for (String connType : listForTypeSeriesCover_Fellwell_series) {
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType : listForTypeSeriesCover_Pure_series) {
            if (isCheck) {
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType : listForTypeSeriesCover_Silicon_case) {
            if (isCheck) {
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType : listForTypeSeriesCover_Delicate_shadow) {
            if (isCheck) {
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType : listForTypeSeriesCover_Fascination_series) {
            if (isCheck) {
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType : listForTypeSeriesCover_Gentle_series) {
            if (isCheck) {
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType : listForTypeSeriesCover_Minni_series) {
            if (isCheck) {
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType : listForTypeSeriesCover_Kingdom_series) {
            if (isCheck) {
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType : listForTypeSeriesCover_Light_series) {
            if (isCheck) {
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        for (String connType : listForTypeSeriesCover_Thin_series) {
            if (isCheck) {
                break;
            }
            if (description.toLowerCase().contains(connType)) {
                isCheck = true;
                break;
            }
        }
        return isCheck;
    }

    public static String replaceNoValidConcurrence(String textForAnalise){
        boolean check1 = textForAnalise.contains("Apple Android");
        boolean check2 = textForAnalise.contains("для зарядки iPhone Lightning");
        boolean check3 = textForAnalise.contains("зарядка iphone 4,5,6,7,8,Х,11,12");
        textForAnalise = textForAnalise.replaceAll("Apple Android", " ");
        textForAnalise = textForAnalise.replaceAll("iPhone Lightning", " ");
        textForAnalise = textForAnalise.replaceAll("зарядка iphone 4,5,6,7,8,Х,11,12", " ");

        return textForAnalise;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static {

        listForCharging.add("микро");
        listForCharging.add("micro");
        listForCharging.add("apple 8 pin");
        listForCharging.add("apple");
        listForCharging.add("8 pin");
        listForCharging.add("lightning");
        listForCharging.add("lighting");
        listForCharging.add("iphone");
        listForCharging.add("type");

        listForHeadset.add("apple");
        listForHeadset.add("lightning");
        //listForHeadset.add("8");
        listForHeadset.add("pin");
        listForHeadset.add("8-pin");
        listForHeadset.add("type-c");
        listForHeadset.add("3,5");
        listForHeadset.add("3.5");

        listForCable.add("0.2м");
        listForCable.add("0.25м");
        listForCable.add("1.0м");
        listForCable.add("1м");
        listForCable.add("1.2м");
        listForCable.add("1.4м");
        listForCable.add("1.6м");
        listForCable.add("1.8м");
        listForCable.add("2.0м");
        listForCable.add("3.0м");

        listForCableAllLength.add("20 см");
        listForCableAllLength.add("0.2м");
        listForCableAllLength.add("0.2 м");
        listForCableAllLength.add("0.2m");
        listForCableAllLength.add("0.2 m");
        listForCableAllLength.add("0,2м");
        listForCableAllLength.add("0,2 м");
        listForCableAllLength.add("0,2m");
        listForCableAllLength.add("0,2 m");
        listForCableAllLength.add("25 см");
        listForCableAllLength.add("0.25м");
        listForCableAllLength.add("0.25 м");
        listForCableAllLength.add("0.25m");
        listForCableAllLength.add("0.25 m");
        listForCableAllLength.add("0,25м");
        listForCableAllLength.add("0,25 м");
        listForCableAllLength.add("0,25m");
        listForCableAllLength.add("0,25 m");
        listForCableAllLength.add("100 см");
        listForCableAllLength.add("1м");
        listForCableAllLength.add("1 м");
        listForCableAllLength.add("1m");
        listForCableAllLength.add("1 m");
        listForCableAllLength.add("1.0м");
        listForCableAllLength.add("1.0 м");
        listForCableAllLength.add("1.0m");
        listForCableAllLength.add("1.0 m");
        listForCableAllLength.add("120 см");
        listForCableAllLength.add("1.2м");
        listForCableAllLength.add("1.2 м");
        listForCableAllLength.add("1.2m");
        listForCableAllLength.add("1.2 m");
        listForCableAllLength.add("1,2м");
        listForCableAllLength.add("1,2 м");
        listForCableAllLength.add("1,2m");
        listForCableAllLength.add("1,2 m");
        listForCableAllLength.add("1.2");
        listForCableAllLength.add("1,2");
        listForCableAllLength.add("140 см");
        listForCableAllLength.add("1.4м");
        listForCableAllLength.add("1.4 м");
        listForCableAllLength.add("1.4m");
        listForCableAllLength.add("1.4 m");
        listForCableAllLength.add("1,4м");
        listForCableAllLength.add("1,4 м");
        listForCableAllLength.add("1,4m");
        listForCableAllLength.add("1,4 m");
        listForCableAllLength.add("1.6м");
        listForCableAllLength.add("1.6 м");
        listForCableAllLength.add("1.6m");
        listForCableAllLength.add("1.6 m");
        listForCableAllLength.add("1,6м");
        listForCableAllLength.add("1,6 м");
        listForCableAllLength.add("1,6m");
        listForCableAllLength.add("1,6 m");
        listForCableAllLength.add("180 см");
        listForCableAllLength.add("1.8м");
        listForCableAllLength.add("1.8 м");
        listForCableAllLength.add("1.8m");
        listForCableAllLength.add("1.8 m");
        listForCableAllLength.add("1,8м");
        listForCableAllLength.add("1,8 м");
        listForCableAllLength.add("1,8m");
        listForCableAllLength.add("1,8 m");
        listForCableAllLength.add("200 см");
        listForCableAllLength.add("2м");
        listForCableAllLength.add("2 м");
        listForCableAllLength.add("2m");
        listForCableAllLength.add("2 m");
        listForCableAllLength.add("2.0м");
        listForCableAllLength.add("2.0 м");
        listForCableAllLength.add("2.0m");
        listForCableAllLength.add("2.0 m");
        listForCableAllLength.add("2,0м");
        listForCableAllLength.add("2,0 м");
        listForCableAllLength.add("2,0m");
        listForCableAllLength.add("2,0 m");
        listForCableAllLength.add("300 см");
        listForCableAllLength.add("3м");
        listForCableAllLength.add("3 м");
        listForCableAllLength.add("3m");
        listForCableAllLength.add("3 m");
        listForCableAllLength.add("3.0м");
        listForCableAllLength.add("3.0 м");
        listForCableAllLength.add("3.0m");
        listForCableAllLength.add("3.0 m");
        listForCableAllLength.add("3,0м");
        listForCableAllLength.add("3,0 м");
        listForCableAllLength.add("3,0m");
        listForCableAllLength.add("3,0 m");



        listForAllTypeConnect.add("микро");
        listForAllTypeConnect.add("micro");
        listForAllTypeConnect.add("apple 8 pin");
        listForAllTypeConnect.add("apple");
        listForAllTypeConnect.add("8 pin");
        listForAllTypeConnect.add("8pin");
        listForAllTypeConnect.add("lightning");
        listForAllTypeConnect.add("iphone");
        listForAllTypeConnect.add("для айфона");
        listForAllTypeConnect.add("type-c");
        listForAllTypeConnect.add("type");

        listForAllTypeConnect.add("2 в 1");
        listForAllTypeConnect.add("2в1");
        listForAllTypeConnect.add("2 in 1");
        listForAllTypeConnect.add("2-in-1");
        listForAllTypeConnect.add("2-in-");
        listForAllTypeConnect.add("2in1");
        listForAllTypeConnect.add("3 в 1");
        listForAllTypeConnect.add("3в1");
        listForAllTypeConnect.add("3-в-1");
        listForAllTypeConnect.add("3 in 1");
        listForAllTypeConnect.add("3-in-1");
        listForAllTypeConnect.add("3-in-");
        listForAllTypeConnect.add("3in1");
        listForAllTypeConnect.add("4 в 1");
        listForAllTypeConnect.add("4в1");
        listForAllTypeConnect.add("4 in 1");
        listForAllTypeConnect.add("4-in-1");
        listForAllTypeConnect.add("4in1");

        listForCable_AUX.add("aux");
        listForCable_AUX.add("аудио кабель");
        listForCable_AUX.add("аудио-кабель");

        listForBugs.add("(S)");
        listForBugs.add("(L)");
        listForBugs.add("13");
        listForBugs.add("16");
        listForBugs.add("5,5");
        listForBugs.add("дюймов");

        listForBrands.add("hoco");
        listForBrands.add("usams");
        listForBrands.add("baseus");
        listForBrands.add("borofone");
        listForBrands.add("celebrat");
        listForBrands.add("dasimei");
        listForBrands.add("mietubl");
        listForBrands.add("xivi");
        listForBrands.add("aiqura");

        listForSeriesCover.add("fellwell series");
        listForSeriesCover.add("pure series");
        listForSeriesCover.add("silicon case");
        listForSeriesCover.add("delicate shadow");
        listForSeriesCover.add("fascination series");
        listForSeriesCover.add("gentle series");
        listForSeriesCover.add("minni series");
        listForSeriesCover.add("kingdom series");
        listForSeriesCover.add("light series");
        listForSeriesCover.add("thin series");
    }

    //for ver. 2.0/additionalParameter//////////////////////////////////////////////////////////////////////////////


    static List<String> getCollectionsParamsForProduct(String param, String url) {
        //для различных типов коннекторов
        List<String> listWithParam = null;
        boolean b = Constants.listForConnector_Micro_USB.contains(param.toLowerCase());
        if (b) {
            listWithParam = Constants.listForConnector_Micro_USB;
        }
        else if (Constants.listForCable_Type_C_to_Type_C.contains(param)) {
            listWithParam = Constants.listForCable_Type_C_to_Type_C;
        } else if (Constants.listForCable_Type_C_to_Apple.contains(param)) {
            listWithParam = Constants.listForCable_Type_C_to_Apple;
        }
        else if (Constants.listForConnector_Apple_8PIN.contains(param)) {
            listWithParam = Constants.listForConnector_Apple_8PIN;
        } else if (Constants.listForConnector_Type_C.contains(param)) {
            listWithParam = Constants.listForConnector_Type_C;
        } else if (Constants.listForConnector_2in1.contains(param)) {
            listWithParam = Constants.listForConnector_2in1;
        } else if (Constants.listForConnector_3in1.contains(param)) {
            listWithParam = Constants.listForConnector_3in1;
        } else if (Constants.listForConnector_4in1.contains(param)) {
            listWithParam = Constants.listForConnector_4in1;
        }  else if (Constants.listForCable_AUX.contains(param)) {
            listWithParam = Constants.listForCable_AUX;
        }

        //для различных длинн кабеля
        else if (Constants.listForCable_0_2m.contains(param)) {
            listWithParam = Constants.listForCable_0_2m;
        } else if (Constants.listForCable_0_25m.contains(param)) {
            listWithParam = Constants.listForCable_0_25m;
        } else if (Constants.listForCable_1m.contains(param)) {
            listWithParam = Constants.listForCable_1m;
        } else if (Constants.listForCable_1_2m.contains(param)) {
            listWithParam = Constants.listForCable_1_2m;
        } else if (Constants.listForCable_1_4m.contains(param)) {
            listWithParam = Constants.listForCable_1_4m;
        } else if (Constants.listForCable_1_6m.contains(param)) {
            listWithParam = Constants.listForCable_1_6m;
        } else if (Constants.listForCable_1_8m.contains(param)) {
            listWithParam = Constants.listForCable_1_8m;
        } else if (Constants.listForCable_2m.contains(param)) {
            listWithParam = Constants.listForCable_2m;
        } else if (Constants.listForCable_3m.contains(param)) {
            listWithParam = Constants.listForCable_3m;
        }

        //для различных защитных стекол
        else if (Constants.listForTypeProtectiveGlass_eye_protection.contains(param)) {
            listWithParam = Constants.listForTypeProtectiveGlass_eye_protection;
        }
        else if (Constants.listForTypeProtectiveGlass_5d.contains(param)) {
            listWithParam = Constants.listForTypeProtectiveGlass_5d;
        }
        else if (Constants.listForTypeProtectiveGlass_2_5d.contains(param)) {
            listWithParam = Constants.listForTypeProtectiveGlass_2_5d;
        }
        else if (Constants.listForTypeProtectiveGlass_11d.contains(param)) {
            listWithParam = Constants.listForTypeProtectiveGlass_11d;
        }
        else if (Constants.listForTypeProtectiveGlass_superd.contains(param)) {
            listWithParam = Constants.listForTypeProtectiveGlass_superd;
        }
        else if (Constants.listForTypeProtectiveGlass_privacy.contains(param)) {
            listWithParam = Constants.listForTypeProtectiveGlass_privacy;
        }

        //если нет такой коллекции, то возвращаем коллекцию с одним этим параметром
        else {
            listWithParam = new ArrayList<>(Collections.singleton(param));
        }
        return listWithParam;
    }

    static List<String> getCollectionsParamCable(String param, String url) {
        //определяем коллекцию с названием одного и того же кабеля
        List<String> listWithParam = null;
        boolean b = Constants.listForConnector_Micro_USB.contains(param);

        if (b) {
            listWithParam = Constants.listForConnector_Micro_USB;
        } else if (Constants.listForConnector_Apple_8PIN.contains(param)) {
            listWithParam = Constants.listForConnector_Apple_8PIN;
        } else if (Constants.listForConnector_Type_C.contains(param)) {
            listWithParam = Constants.listForConnector_Type_C;
        } else if (Constants.listForConnector_2in1.contains(param)) {
            listWithParam = Constants.listForConnector_2in1;
        } else if (Constants.listForConnector_3in1.contains(param)) {
            listWithParam = Constants.listForConnector_3in1;
        } else if (Constants.listForConnector_4in1.contains(param)) {
            listWithParam = Constants.listForConnector_4in1;
        } else if (Constants.listForCable_0_2m.contains(param)) {
            listWithParam = Constants.listForCable_0_2m;
        } else if (Constants.listForCable_0_25m.contains(param)) {
            listWithParam = Constants.listForCable_0_25m;
        } else if (Constants.listForCable_1m.contains(param)) {
            listWithParam = Constants.listForCable_1m;
        } else if (Constants.listForCable_1_2m.contains(param)) {
            listWithParam = Constants.listForCable_1_2m;
        } else if (Constants.listForCable_1_4m.contains(param)) {
            listWithParam = Constants.listForCable_1_4m;
        } else if (Constants.listForCable_1_6m.contains(param)) {
            listWithParam = Constants.listForCable_1_6m;
        } else if (Constants.listForCable_1_8m.contains(param)) {
            listWithParam = Constants.listForCable_1_8m;
        } else if (Constants.listForCable_2m.contains(param)) {
            listWithParam = Constants.listForCable_2m;
        } else if (Constants.listForCable_3m.contains(param)) {
            listWithParam = Constants.listForCable_3m;
        } else if (Constants.listForCable_Type_C_to_Type_C.contains(param)) {
            listWithParam = Constants.listForCable_Type_C_to_Type_C;
        } else if (Constants.listForCable_Type_C_to_Apple.contains(param)) {
            listWithParam = Constants.listForCable_Type_C_to_Apple;
        } else if (Constants.listForCable_AUX.contains(param)) {
            listWithParam = Constants.listForCable_AUX;
        } else {
            System.out.println("Уточнить параметр кабеля для ссылки \"" + url + "\"");
        }
        return listWithParam;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static List<String> getCollectionsParam(List<List<String>> arrayParams, String url) {
        //определяем коллекцию с названием одного и того же кабеля
        List<String> listWithCable = null;
        if (arrayParams.size() == 1){
            String param = arrayParams.get(0).get(0);
            boolean b = Constants.listForConnector_Micro_USB.contains(param);

            if (b){
                listWithCable = Constants.listForConnector_Micro_USB;
            } else if (Constants.listForConnector_Apple_8PIN.contains(param)){
                listWithCable = Constants.listForConnector_Apple_8PIN;
            } else if (Constants.listForConnector_Type_C.contains(param)){
                listWithCable = Constants.listForConnector_Type_C;
            } else {
                System.out.println("Уточнить параметр кабеля для ссылки \"" + url + "\"");
            }
            return listWithCable;
        } else {
            return  null;
        }
    }

    static String getRedString(String s){
        /*
        Если хочешь другой цвет, то измени "[31mWarning!". Например, на "[35mWarning!". Текст будет пурпурным.
        30 - черный. 31 - красный. 32 - зеленый. 33 - желтый. 34 - синий. 35 - пурпурный. 36 - голубой. 37 - белый.
         */
        return (char) 27 + "[31m" + s + (char)27 + "[0m";
    }

    static String getBlueString(String s){
        /*
        Если хочешь другой цвет, то измени "[31mWarning!". Например, на "[35mWarning!". Текст будет пурпурным.
        30 - черный. 31 - красный. 32 - зеленый. 33 - желтый. 34 - синий. 35 - пурпурный. 36 - голубой. 37 - белый.
         */
        return (char) 27 + "[34m" + s + (char)27 + "[0m";
    }

    static String getGreenString(String s){
        /*
        Если хочешь другой цвет, то измени "[31mWarning!". Например, на "[35mWarning!". Текст будет пурпурным.
        30 - черный. 31 - красный. 32 - зеленый. 33 - желтый. 34 - синий. 35 - пурпурный. 36 - голубой. 37 - белый.
         */
        return (char) 27 + "[32m" + s + (char)27 + "[0m";
    }

    static String getYellowString(String s){
        /*
        Если хочешь другой цвет, то измени "[31mWarning!". Например, на "[35mWarning!". Текст будет пурпурным.
        30 - черный. 31 - красный. 32 - зеленый. 33 - желтый. 34 - синий. 35 - пурпурный. 36 - голубой. 37 - белый.
         */
        return (char) 27 + "[33m" + s + (char)27 + "[0m";
    }

    static void addedParamForSeriesCover(List<String> arrayParams, String myNomenclature) {
        for (String series : Constants.listForSeriesCover) {
            if (myNomenclature.replaceAll(",", "").toLowerCase().contains(series)) {
                arrayParams.add(series);
            }
        }
    }

    static void addedParamForCableLenght(List<String> arrayParams, String myNomenclature) {
        for (String type : Constants.listForCable){
            if (myNomenclature.replaceAll(",", "").contains(type)) {
                if (type.contains("1.0")){
                    arrayParams.add("1 м");
                } else if (type.contains("2.0")){
                    arrayParams.add("2 м");
                } else if (type.contains("3.0")){
                    arrayParams.add("3 м");
                } else {
                    arrayParams.add(type);
                }
            }
        }
    }
}
