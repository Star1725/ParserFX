import java.util.ArrayList;

public class Constants {

    public static final String CATEGORY_5 = "Держатели в авто";//5
    public static final String CATEGORY_13 = "Пылесосы автомобильные";//5
    public static final String CATEGORY_18 = "Подставки для мобильных устройств";//5

    public static final String CATEGORY_2 = "Внешние аккумуляторы";//7
    public static final String CATEGORY_15 = "Переходники";//7

    public static final String CATEGORY_1 = "Автомобильные зарядные устройства";//12
    public static final String CATEGORY_3 = "Гарнитуры";//12
    public static final String CATEGORY_6 = "Зарядные устройства";//12
    public static final String CATEGORY_7 = "Защитные стекла";//12
    public static final String CATEGORY_8 = "Кабели";//12
    public static final String CATEGORY_9 = "Колонки";//12
    public static final String CATEGORY_11 = "Моноподы";//12
    public static final String CATEGORY_12 = "Наушники";//12
    public static final String CATEGORY_14 = "Увлажнители";//12
    public static final String CATEGORY_16 = "Термометры медицинские";//12
    public static final String CATEGORY_17 = "Адаптеры";//12
    public static final String CATEGORY_24 = "Мусорные ведра";//12
    public static final String CATEGORY_25 = "Мухобойки механические";//12
    public static final String CATEGORY_26 = "Мыльницы";//12
    public static final String CATEGORY_27 = "Подсветка для ноутбука";//12
    public static final String CATEGORY_28 = "Подстаканники электрические";//12
    public static final String CATEGORY_29 = "Подушки автомобильные";//12
    public static final String CATEGORY_30 = "Пульты дистанционной съемки";//12
    public static final String CATEGORY_32 = "Салфетки для авто";//12
    public static final String CATEGORY_33 = "Светильники";//12
    public static final String CATEGORY_35 = "Таблички для авто";//12
    public static final String CATEGORY_36 = "Фильтры воздушные";//12
    public static final String CATEGORY_37 = "Автомобильные ароматизаторы";//12
    public static final String CATEGORY_38 = "FM-трансмиттеры";//12
    public static final String CATEGORY_39 = "Автохимия";//12
    public static final String CATEGORY_40 = "Насадки для швабр";//12

    public static final String CATEGORY_4 = "Гироскутеры";//15
    public static final String CATEGORY_10 = "Маски одноразовые";//15
    public static final String CATEGORY_19 = "Чехлы для телефонов";//15
    public static final String CATEGORY_20 = "Дезинфицирующие средства";//15
    public static final String CATEGORY_21 = "Защитные кейсы";//15
    public static final String CATEGORY_22 = "Лампы автомобильные";//15
    public static final String CATEGORY_23 = "Массажеры электрические";//15
    public static final String CATEGORY_31 = "Ремешки для умных часов";//15
    public static final String CATEGORY_34 = "Сумки для ноутбуков";//15

    public static final String CATEGORY_41 = "Молотки";//15
    public static final String CATEGORY_42 = "Облучатели-рециркуляторы";//15
    public static final String CATEGORY_43 = "Пепельницы";//15
    public static final String CATEGORY_44 = "Подсветка автомобильная";//15
    public static final String CATEGORY_45 = "Подставки для бытовой техники";//15
    public static final String CATEGORY_46 = "Ресиверы";//15
    public static final String CATEGORY_47 = "Стилусы";//15
    public static final String CATEGORY_48 = "Насосы автомобильные";//15



    public static final String MARKETPLACE = "https://www.wildberries.ru";

    public static final String NOT_FOUND_PAGE = "Превышено время ожидания ответа сервера либо не найдена страница товара";
    public static final String NOT_FOUND_HTML_ITEM = "html item not found";

    public static final String PARAM_1_1 = "Количество предметов в упаковке";
    public static final String PARAM_1_2 = "Модель";
    public static final String PARAM_1_3 = "Гарантийный срок";

    //Html элементы страницы моего товара
    public static final String ELEMENT_WITH_SPEC_ACTION_MY_PRODUCT = "div[class=i-spec-action-v1 ]";
    public static final String ELEMENT_WITH_PHOTO_MY_PRODUCT = "img[class=preview-photo j-zoom-preview]";
    public static final String ELEMENT_WITH_TITLE_MY_PRODUCT = "div[class=brand-and-name j-product-title]";
    public static final String ELEMENT_WITH_PARAMS_MY_PRODUCT = "div[class=params]";
    public static final String ELEMENT_WITH_DESCRIPTION_MY_PRODUCT = "div[class=j-description collapsable-content description-text]";

    //Html элементы страницы поискового запроса аналогов
    public static final String ELEMENT_WITH_CATALOG = "div[class=catalog_main_table j-products-container]";
    public static final String ELEMENT_WITH_PRODUCT = "div[class=dtList i-dtList j-card-item]";
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

    public static final String URL_FOR_JSON = "https://wbxcatalog-ru.wildberries.ru/nm-2-card/catalog?spp=0&regions=69,58,64,40,48,70,1,38,4,30,22,66&" +
            "stores=119261,121631,1193,119400,116433,117501,507,3158,120762,1733,117986,686,117413,119781&" +
            "couponsGeo=2,12,6,9&pricemarginCoeff=1.0&reg=0&appType=1&offlineBonus=0&onlineBonus=0&emp=0&locale=ru&lang=ru&nm=";

    //статика для проверки заговков столбцов файла отчёта Wildberies
    public static final String BRAND_NAME_IN_FILE_WILDBERIES = "Бренд";//0-й столбец
    public static final String CATEGORY_NAME_IN_FILE_WILDBERIES = "Предмет";//1-й столбец
    public static final String CODE_1C_IN_FILE_WILDBERIES = "Артикул поставщика";//3-й столбец
    public static final String VENDOR_CODE_IN_FILE_WILDBERIES = "Номенклатура (код 1С)";//4-й столбец
    public static final String VENDOR_CODE_1C_IN_FILE_WILDBERIES = "Последний баркод";//5-й столбец
    public static final String PRICE_U_IN_FILE_WILDBERIES = "Текущая розн. цена (до скидки)";//11-й столбец
    public static final String BASIC_SALE_IN_FILE_WILDBERIES = "Текущая скидка на сайте, %";//13-й столбец
    public static final String PROMO_SALE_IN_FILE_WILDBERIES = "Текущая скидка по промокоду, %";//16-й столбец

    //статика для проверки заговков столбцов файла отчёта 1С
    public static final String CODE_1C = "Код";//0-й столбец
    public static final String VENDOR_CODE_1C = "Артикул";//1-й столбец
    public static final String SPEC_PRICE_1C = "Цена";//5-й столбец

    public static final ArrayList<String> listForCabel = new ArrayList<>();
    //public static final ArrayList<String> listForLength = new ArrayList<>();
    public static final ArrayList<String> listForTypeGlass = new ArrayList<>();
    public static final ArrayList<String> listForСharging = new ArrayList<>();
    public static final ArrayList<String> listForBugs = new ArrayList<>();
    public static final ArrayList<String> listForHeadset = new ArrayList<>();


    static {
        listForHeadset.add("apple");
        listForHeadset.add("lightning");
        //listForHeadset.add("8");
        listForHeadset.add("pin");
        listForHeadset.add("8-pin");
        listForHeadset.add("type-c");
        listForHeadset.add("3,5");
        listForHeadset.add("3.5");

        listForCabel.add("aux");
        listForCabel.add("jack");
        listForCabel.add("type-c");
        listForCabel.add("apple");
//        listForTypeConnect.add("8-pin");
//        listForTypeConnect.add("8 pin");
        listForCabel.add("микро");
        listForCabel.add("micro");
        listForCabel.add("угловой");
        listForCabel.add("lightning");
        listForCabel.add("0.2м");
        listForCabel.add("0.25м");
        listForCabel.add("1.0м");
        listForCabel.add("1м");
        listForCabel.add("1.2м");
        listForCabel.add("1.4м");
        listForCabel.add("1.6м");
        listForCabel.add("1.8м");
        listForCabel.add("2.0м");
        listForCabel.add("3.0м");

        listForTypeGlass.add("EYE PROTECTION");
        listForTypeGlass.add("5D");
        listForTypeGlass.add("11D");
        listForTypeGlass.add("2.5D");
        listForTypeGlass.add("SuperD");
        listForTypeGlass.add("Super D");
        listForTypeGlass.add("EYE");
        listForTypeGlass.add("Privacy");

        listForСharging.add("держателем");
        listForСharging.add("для");
        listForСharging.add("авто");
        listForСharging.add("микро");
        listForСharging.add("Micro");
        listForСharging.add("micro");
        //listForСharging.add("USB");
        listForСharging.add("Apple");
        //listForСharging.add("8");
        //listForСharging.add("pin");
        listForСharging.add("Type-C");

        listForBugs.add("(S)");
        listForBugs.add("(L)");
        listForBugs.add("13");
        listForBugs.add("16");
        listForBugs.add("5,5");
        listForBugs.add("дюймов");
    }
}
