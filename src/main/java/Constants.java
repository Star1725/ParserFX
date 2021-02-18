import java.util.ArrayList;
import java.util.Arrays;

public class Constants {
    public static final String MARKETPLACE = "https://www.wildberries.ru";

    public static final String NOT_FOUND_PAGE = "Превышено время ожидания ответа сервера либо не найдена страница товара";

    public static final String ELEMENT_WITH_CATALOG = "div[class=catalog_main_table j-products-container]";
    public static final String ELEMENT_WITH_PRODUCT = "div[class=dtList i-dtList j-card-item]";
    public static final String ATTRIBUTE_WITH_VENDOR_CODE = "data-popup-nm-id";
    public static final String ELEMENT_WITH_CARD_PRODUCT = "a[class=ref_goods_n_p j-open-full-product-card]";
    public static final String ELEMENT_WITH_NAME_PRODUCT = "span[class=goods-name c-text-sm]";
    public static final String ATTRIBUTE_WITH_REF_FOR_PAGE_PRODUCT = "href";
    public static final String ELEMENT_WITH_REF_FOR_IMAGE = "img[class=thumbnail]";
    public static final String ATTRIBUTE_WITH_REF_FOR_IMAGE_1 = "src";
    public static final String ATTRIBUTE_WITH_REF_FOR_IMAGE_2 = "data-original";
    public static final String ELEMENT_WITH_SPEC_ACTION = "span[class=spec-actions-catalog i-spec-action]";

    public static final String URL_FOR_JSON = "https://wbxcatalog-ru.wildberries.ru/nm-2-card/catalog?spp=0&regions=69,58,64,40,48,70,1,38,4,30,22,66&" +
            "stores=119261,121631,1193,119400,116433,117501,507,3158,120762,1733,117986,686,117413,119781&" +
            "couponsGeo=2,12,6,9&pricemarginCoeff=1.0&reg=0&appType=1&offlineBonus=0&onlineBonus=0&emp=0&locale=ru&lang=ru&nm=";

    public static final String BRAND_NAME = "Бренд";


    public static final ArrayList<String> listForTypeConnect = new ArrayList<>();
    public static final ArrayList<String> listForTypeGlass = new ArrayList<>();
    public static final ArrayList<String> listForCabel = new ArrayList<>();
    public static final String CATEGORY_NAME = "Предмет";
    public static final String CODE_1C = "Артикул поставщика";
    public static final String VENDOR_CODE = "Номенклатура (код 1С)";
    public static final String VENDOR_CODE_1C = "Последний баркод";
    public static final String PRICE_U = "Текущая розн. цена (до скидки)";

    static {
        listForTypeConnect.add("Type-C");
        listForTypeConnect.add("8-pin");
        listForTypeConnect.add("8 pin");
        listForTypeConnect.add("микро USB");
        listForTypeConnect.add("Apple");
        listForTypeConnect.add("угловой");
        listForTypeConnect.add("lightning");
        listForTypeConnect.add("Lightning");

        listForTypeGlass.add("EYE PROTECTION");
        listForTypeGlass.add("5D");
        listForTypeGlass.add("11D");
        listForTypeGlass.add("2.5D");
        listForTypeGlass.add("SuperD");
        listForTypeGlass.add("EYE");
        listForTypeGlass.add("Privacy");

        listForCabel.add("кабелем");
        listForCabel.add("кабель");
        listForCabel.add("Микро");
        listForCabel.add("микро");
        listForCabel.add("Micro");
        listForCabel.add("micro");
        listForCabel.add("USB");
        listForCabel.add("Apple");
        listForCabel.add("pin");
        listForCabel.add("Type-C");
    }
}
