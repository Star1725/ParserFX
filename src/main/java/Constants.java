import java.util.ArrayList;

public class Constants {
    //настройки для прокси-сервера
//    public static final String PROXY_HOST = "svetlana.ltespace.com";
//    public static final int PROXY_PORT = 16565;
//    public static final String LOGIN = "4soy9fg6";
//    public static final String PASSWORD = "boxhyr84";

    public static final String PROXY_HOST = "marina.ltespace.com";
    public static final int PROXY_PORT = 12426;
    public static final String LOGIN = "6uxxte5v";
    public static final String PASSWORD = "x1k7dfkc";

    public static final String URL_FOR_SWITCH_IP = "https://ltespace.com/api/private/touch?port=" + PROXY_PORT + "&login=" + LOGIN + "&password=" + PASSWORD;
                                                  //https://ltespace.com/api/private/touch?port=18487&login=pttgua83&password=xr791oom

    //marketplaces
    public static final String WILDBERIES = "Wildberies";
    public static final String OZON = "Ozon";

    //уровень понижения цены
    public static final String RUB = "₽";
    public static final String PERCENT = "%";

    //заказчик
    public static final String MY_SELLER = "Продавец ООО «АКСЕСС»";

    public static final String BLOCKING = "блокировка сервером";


    //категории для Wildberies с наценкой
    public static final String CATEGORY_WILD_5 = "Держатели в авто";//5
    public static final String CATEGORY_WILD_13 = "Пылесосы автомобильные";//5
    public static final String CATEGORY_WILD_18 = "Подставки для мобильных устройств";//5

    public static final String CATEGORY_WILD_2 = "Внешние аккумуляторы";//7
    public static final String CATEGORY_WILD_15 = "Переходники";//7

    public static final String CATEGORY_WILD_1 = "Автомобильные зарядные устройства";//12
    public static final String CATEGORY_WILD_3 = "Гарнитуры";//12
    public static final String CATEGORY_WILD_6 = "Зарядные устройства";//12
    public static final String CATEGORY_WILD_7 = "Защитные стекла";//12
    public static final String CATEGORY_WILD_8 = "Кабели";//12
    public static final String CATEGORY_WILD_9 = "Колонки";//12
    public static final String CATEGORY_WILD_11 = "Моноподы";//12
    public static final String CATEGORY_WILD_12 = "Наушники";//12
    public static final String CATEGORY_WILD_14 = "Увлажнители";//12
    public static final String CATEGORY_WILD_16 = "Термометры медицинские";//12
    public static final String CATEGORY_WILD_17 = "Адаптеры";//12
    public static final String CATEGORY_WILD_24 = "Мусорные ведра";//12
    public static final String CATEGORY_WILD_25 = "Мухобойки механические";//12
    public static final String CATEGORY_WILD_26 = "Мыльницы";//12
    public static final String CATEGORY_WILD_27 = "Подсветка для ноутбука";//12
    public static final String CATEGORY_WILD_28 = "Подстаканники электрические";//12
    public static final String CATEGORY_WILD_29 = "Подушки автомобильные";//12
    public static final String CATEGORY_WILD_30 = "Пульты дистанционной съемки";//12
    public static final String CATEGORY_WILD_32 = "Салфетки для авто";//12
    public static final String CATEGORY_WILD_33 = "Светильники";//12
    public static final String CATEGORY_WILD_35 = "Таблички для авто";//12
    public static final String CATEGORY_WILD_36 = "Фильтры воздушные";//12
    public static final String CATEGORY_WILD_37 = "Автомобильные ароматизаторы";//12
    public static final String CATEGORY_WILD_38 = "FM-трансмиттеры";//12
    public static final String CATEGORY_WILD_39 = "Автохимия";//12
    public static final String CATEGORY_WILD_40 = "Насадки для швабр";//12

    public static final String CATEGORY_WILD_4 = "Гироскутеры";//15
    public static final String CATEGORY_WILD_10 = "Маски одноразовые";//15
    public static final String CATEGORY_WILD_19 = "Чехлы для телефонов";//15
    public static final String CATEGORY_WILD_20 = "Дезинфицирующие средства";//15
    public static final String CATEGORY_WILD_21 = "Защитные кейсы";//15
    public static final String CATEGORY_WILD_22 = "Лампы автомобильные";//15
    public static final String CATEGORY_WILD_23 = "Массажеры электрические";//15
    public static final String CATEGORY_WILD_31 = "Ремешки для умных часов";//15
    public static final String CATEGORY_WILD_34 = "Сумки для ноутбуков";//15
    public static final String CATEGORY_WILD_41 = "Молотки";//15
    public static final String CATEGORY_WILD_42 = "Облучатели-рециркуляторы";//15
    public static final String CATEGORY_WILD_43 = "Пепельницы";//15
    public static final String CATEGORY_WILD_44 = "Подсветка автомобильная";//15
    public static final String CATEGORY_WILD_45 = "Подставки для бытовой техники";//15
    public static final String CATEGORY_WILD_46 = "Ресиверы";//15
    public static final String CATEGORY_WILD_47 = "Стилусы";//15
    public static final String CATEGORY_WILD_48 = "Насосы автомобильные";//15

    //категории по 1С
    public static final String PRODUCT_TYPE_1C_1   = "FM-трансмиттер";
    public static final String PRODUCT_TYPE_1C_2   = "USB-концентратор";
    public static final String PRODUCT_TYPE_1C_3   = "USB-хаб";
    //public static final String PRODUCT_TYPE_1C_4   = "USB- хаб";
    public static final String PRODUCT_TYPE_1C_5   = "Автовизитка";
    public static final String PRODUCT_TYPE_1C_6   = "Автомобильная лампа";
    public static final String PRODUCT_TYPE_1C_7   = "Автомобильная пепельница";
    public static final String PRODUCT_TYPE_1C_8   = "Автомобильная рулонная штора";
    public static final String PRODUCT_TYPE_1C_9   = "Автомобильная корзина для мусора";
    public static final String PRODUCT_TYPE_1C_10  = "Автомобильное зарядное устройство";
    public static final String PRODUCT_TYPE_1C_11  ="Автомобильное полотенце";
    public static final String PRODUCT_TYPE_1C_12  ="Автомобильный Bluetooth-приемник";
    public static final String PRODUCT_TYPE_1C_13  ="держатель";
    public static final String PRODUCT_TYPE_1C_15  ="Автомобильный контейнер для мусора";
    public static final String PRODUCT_TYPE_1C_16  ="Автомобильный крюк";
    public static final String PRODUCT_TYPE_1C_17  ="Автомобильный крючок";
    public static final String PRODUCT_TYPE_1C_18  ="Автомобильный очиститель воздуха";
    public static final String PRODUCT_TYPE_1C_19  ="Автомобильный держатель";
    public static final String PRODUCT_TYPE_1C_20  ="Автомобильный подголовник";
    public static final String PRODUCT_TYPE_1C_21  ="Автомобильный светильник";
    public static final String PRODUCT_TYPE_1C_22  ="Автомобильный стеклоочиститель";
    public static final String PRODUCT_TYPE_1C_23  ="Автомобильный увлажнитель воздуха";
    public static final String PRODUCT_TYPE_1C_24  ="Адаптер";
    public static final String PRODUCT_TYPE_1C_25  ="Аккумулятор внешний";
    public static final String PRODUCT_TYPE_1C_26  ="Аккумуляторный светильник";
    public static final String PRODUCT_TYPE_1C_27  ="Ароматизатор автомобильный";
    public static final String PRODUCT_TYPE_1C_28  ="Ароматизатор для автомобиля";
    public static final String PRODUCT_TYPE_1C_29 ="Ароматизатор воздуха Автомобильный";
    public static final String PRODUCT_TYPE_1C_30 ="Ароматизатор";
    public static final String PRODUCT_TYPE_1C_31 ="Аудио-адаптер Apple 8 pin";
    public static final String PRODUCT_TYPE_1C_32 ="Аудио-переходник  Apple 8 pin - Apple 8 pin(f)";
    public static final String PRODUCT_TYPE_1C_33 ="Аудио-переходник Type-C - Type-C(f)";
    public static final String PRODUCT_TYPE_1C_34 ="Бампер";
    public static final String PRODUCT_TYPE_1C_35 ="Бесконтактный диспенсер-спрей для рук";
    public static final String PRODUCT_TYPE_1C_36 ="Беспроводная колонка";
    public static final String PRODUCT_TYPE_1C_37 ="Беспроводное зарядное устройство";
    //public static final String PRODUCT_TYPE_1C_38 ="Блок питания сетевой";
    public static final String PRODUCT_TYPE_1C_39 ="Блок питания автомобильный";
    public static final String PRODUCT_TYPE_1C_40 ="Блок питания";
    public static final String PRODUCT_TYPE_1C_41 ="Держатель";
    public static final String PRODUCT_TYPE_1C_42 ="Гарнитур";
    public static final String PRODUCT_TYPE_1C_43 ="Лампа в машину";

    public static final String PRODUCT_TYPE_1C_46 ="Двухрежимный массажный пистолет";
    public static final String PRODUCT_TYPE_1C_48 ="Кабель USB - Apple 8 pin, Type-C, микро USB";
    public static final String PRODUCT_TYPE_1C_49 ="Кабель Type-C - Type-C";
    public static final String PRODUCT_TYPE_1C_50 ="Кабель Type-C - Apple 8 pin";
    public static final String PRODUCT_TYPE_1C_51 ="Кабель HDMI";
    public static final String PRODUCT_TYPE_1C_52 ="Кабель 4KHD";
    public static final String PRODUCT_TYPE_1C_53 ="Кабель 8 pin";
    public static final String PRODUCT_TYPE_1C_54 ="Дозатор";
    public static final String PRODUCT_TYPE_1C_55 ="Док-станция";
    public static final String PRODUCT_TYPE_1C_56 ="Зажим для очков";
    public static final String PRODUCT_TYPE_1C_57 ="Зарядная подставка";
    public static final String PRODUCT_TYPE_1C_58 ="Зарядная станция";
    public static final String PRODUCT_TYPE_1C_59 ="Защитный молоток";
    public static final String PRODUCT_TYPE_1C_60 ="Защитный чехол";

    public static final String PRODUCT_TYPE_1C_61 ="Кабель AUX";
    public static final String PRODUCT_TYPE_1C_62 ="Кабель Type-C - 8 pin";

    public static final String PRODUCT_TYPE_1C_63 ="Кабель USB - 8 pin";
    public static final String PRODUCT_TYPE_1C_64 ="Кабель USB - Apple 8 pin";
    public static final String PRODUCT_TYPE_1C_65 ="Кабель USB - Type-C";
    public static final String PRODUCT_TYPE_1C_66 ="Кабель USB - микро USB";
    public static final String PRODUCT_TYPE_1C_67 ="Кабель VGA";
    public static final String PRODUCT_TYPE_1C_68 ="Кабель USB Series 2-in";//в запросе заменить на Кабель USB 2 в 1
    public static final String PRODUCT_TYPE_1C_69 ="Кабель USB Series 3-in";//в запросе заменить на Кабель USB 3 в 1
    public static final String PRODUCT_TYPE_1C_70 ="Кабель USB Series 4-in";

    public static final String PRODUCT_TYPE_1C_72 ="Картридер";
    public static final String PRODUCT_TYPE_1C_73 ="Козырек от солнца";
    public static final String PRODUCT_TYPE_1C_74 ="Колонка портативная";
    public static final String PRODUCT_TYPE_1C_75 ="Кольцевая LED-лампа для селфи";
    public static final String PRODUCT_TYPE_1C_76 ="Контейнер";
    public static final String PRODUCT_TYPE_1C_77 ="Крючок для телефона";
    public static final String PRODUCT_TYPE_1C_78 ="Маска";
    public static final String PRODUCT_TYPE_1C_79 ="Магнитный кабель USB - Apple 8 pin";
    public static final String PRODUCT_TYPE_1C_83 ="Магнитный кабель USB - Type-C";
    public static final String PRODUCT_TYPE_1C_84 ="Магнитный кабель USB - микро USB";
    public static final String PRODUCT_TYPE_1C_85 ="Магнитный кабель USB Series 3-in-";//в запросе заменить на Магнитный кабель USB 3 в 1
    public static final String PRODUCT_TYPE_1C_86 ="Мини-холодильник";
    public static final String PRODUCT_TYPE_1C_87 ="Мусорный бак";
    public static final String PRODUCT_TYPE_1C_88 ="Мини-штатив";
    public static final String PRODUCT_TYPE_1C_89 ="Многофункциональный стерилизатор";
    public static final String PRODUCT_TYPE_1C_90 ="Монопод для селфи";
    public static final String PRODUCT_TYPE_1C_91 ="Набор для чистки автомобиля";
    public static final String PRODUCT_TYPE_1C_92 ="Накладка для телефона";
    public static final String PRODUCT_TYPE_1C_93 ="Накладка задняя";
    public static final String PRODUCT_TYPE_1C_94 ="Насадка МОП";
    public static final String PRODUCT_TYPE_1C_96 ="Насос автомобильный";
    public static final String PRODUCT_TYPE_1C_97 ="Настольная лампа";
    public static final String PRODUCT_TYPE_1C_98 ="Настольная подставка";
    public static final String PRODUCT_TYPE_1C_99 ="Наушники";
    public static final String PRODUCT_TYPE_1C_100="Ночник переносной";
    public static final String PRODUCT_TYPE_1C_101="Ночник";
    public static final String PRODUCT_TYPE_1C_102="Органайзер для проводов и аксессуаров";
    public static final String PRODUCT_TYPE_1C_103="Переходник Apple 8 pin - Apple 8 pin";
    public static final String PRODUCT_TYPE_1C_104="Переходник Apple 8 pin - микро USB";
    public static final String PRODUCT_TYPE_1C_105="Переходник AUX - Type-C";
    public static final String PRODUCT_TYPE_1C_106="Переходник Jack 3,5mm - Apple 8 pin";
    public static final String PRODUCT_TYPE_1C_107="Переходник Type-C - Jack 3,5mm";
    public static final String PRODUCT_TYPE_1C_108="Переходник Type-C - Type-C";
    public static final String PRODUCT_TYPE_1C_109="Переходник Type-C - USB";
    public static final String PRODUCT_TYPE_1C_110="Переходник Type-C - микро USB";
    public static final String PRODUCT_TYPE_1C_111="Переходник микро USB - 8 pin";
    public static final String PRODUCT_TYPE_1C_112="Переходник микро USB - Type-C";
    public static final String PRODUCT_TYPE_1C_113="Переходник микро USB - USB 3.0";
    public static final String PRODUCT_TYPE_1C_114="Переходник микро USB - USB";
    public static final String PRODUCT_TYPE_1C_115="Переходник";
    public static final String PRODUCT_TYPE_1C_116="Подсветка экрана";
    public static final String PRODUCT_TYPE_1C_117="Подставка под кольцо для телефона";
    public static final String PRODUCT_TYPE_1C_118="Подстаканник для подогрева и охлаждения напитков в автомобиле";
    public static final String PRODUCT_TYPE_1C_119="Подушка для шеи";
    public static final String PRODUCT_TYPE_1C_120="Портативный увлажнитель воздуха";
    public static final String PRODUCT_TYPE_1C_121="Портативный ультрафиолетовый дезинфектор-стерилизатор";
    public static final String PRODUCT_TYPE_1C_122="Пульт дистанционного управления";
    public static final String PRODUCT_TYPE_1C_123="Пылесос автомобильный";
    public static final String PRODUCT_TYPE_1C_124="Разветвитель HDMI";
    public static final String PRODUCT_TYPE_1C_125="Ремешок";
    public static final String PRODUCT_TYPE_1C_126="Ресивер";
    public static final String PRODUCT_TYPE_1C_127="Ручная сумка-чехол для Nintendo Switch";
    public static final String PRODUCT_TYPE_1C_128="Рюкзак для ноутбука";
    public static final String PRODUCT_TYPE_1C_129="Салфетка Автомобильная";
    public static final String PRODUCT_TYPE_1C_130="Светильник";
    public static final String PRODUCT_TYPE_1C_131="Селфи палка-штатив";
    public static final String PRODUCT_TYPE_1C_132="Сетевое зарядное устройство";
    //public static final String PRODUCT_TYPE_1C_133="Сетевое зарядное устройство 2 USB";
    public static final String PRODUCT_TYPE_1C_134="Сетевой разветвитель для кабеля HDMI";
    public static final String PRODUCT_TYPE_1C_135="Сетевой разветвитель для кабеля RJ45";
    public static final String PRODUCT_TYPE_1C_136="Силиконовый чехол";
    public static final String PRODUCT_TYPE_1C_137="Специальный кабель";
    public static final String PRODUCT_TYPE_1C_138="Стабилизатор для камеры";
    public static final String PRODUCT_TYPE_1C_139="Стекло защитное";
    public static final String PRODUCT_TYPE_1C_140="Стилус";
    public static final String PRODUCT_TYPE_1C_142="Сумка для мусора в подстаканник";
    public static final String PRODUCT_TYPE_1C_143="Сумка для ноутбука";
    public static final String PRODUCT_TYPE_1C_144="Сумка для хранения";
    public static final String PRODUCT_TYPE_1C_145="Сумка для цифровых устройств";
    public static final String PRODUCT_TYPE_1C_146="Сумка органайзер для хранения аксессуаров";
    public static final String PRODUCT_TYPE_1C_147="Съемные линзы для камеры";
    public static final String PRODUCT_TYPE_1C_148="Термометр бесконтактный инфракрасный";
    public static final String PRODUCT_TYPE_1C_149="Термоподстаканник";
    public static final String PRODUCT_TYPE_1C_150="Увлажнитель воздуха";
    public static final String PRODUCT_TYPE_1C_151="Ультрафиолетовая лампа";
    public static final String PRODUCT_TYPE_1C_152="Ультрафиолетовый стерилизатор";
    public static final String PRODUCT_TYPE_1C_153="Устройство зарядное беспроводное";
    public static final String PRODUCT_TYPE_1C_154="Устройство зарядное";
    public static final String PRODUCT_TYPE_1C_155="Усилитель сигнала HDMI";
    public static final String PRODUCT_TYPE_1C_156="Фильтр для очистителя воздуха";
    public static final String PRODUCT_TYPE_1C_157="Фильтр для пылесоса";
    public static final String PRODUCT_TYPE_1C_158="Чехол для беспроводной зарядки";
    public static final String PRODUCT_TYPE_1C_159="Чехол для ноутбука";
    public static final String PRODUCT_TYPE_1C_160="Чехол силиконовый для Pods Pro";
    public static final String PRODUCT_TYPE_1C_161="Штатив для селфи";
    public static final String PRODUCT_TYPE_1C_162="Щетка для автомобиля";
    public static final String PRODUCT_TYPE_1C_163="Электрическая мухобойка";
    public static final String PRODUCT_TYPE_1C_164="Автомойка";

    public static final String MARKETPLACE = "https://www.wildberries.ru";

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

    //for json query
    public static final String URL_FOR_JSON = "https://wbxcatalog-ru.wildberries.ru/nm-2-card/catalog?spp=0&regions=69,58,64,40,48,70,1,38,4,30,22,66&" +
            "stores=119261,121631,1193,119400,116433,117501,507,3158,120762,1733,117986,686,117413,119781&" +
            "couponsGeo=2,12,6,9&pricemarginCoeff=1.0&reg=0&appType=1&offlineBonus=0&onlineBonus=0&emp=0&locale=ru&lang=ru&nm=";


    public static final ArrayList<String> listForCabel = new ArrayList<>();
    //public static final ArrayList<String> listForLength = new ArrayList<>();
    public static final ArrayList<String> listForTypeGlass = new ArrayList<>();
    public static final ArrayList<String> listForCharging = new ArrayList<>();
    public static final ArrayList<String> listForBugs = new ArrayList<>();
    public static final ArrayList<String> listForHeadset = new ArrayList<>();
    public static final ArrayList<String> listForBrands = new ArrayList<>();
    public static final ArrayList<String> listForCategoryBy_1C = new ArrayList<>();


    static {
        listForHeadset.add("apple");
        listForHeadset.add("lightning");
        //listForHeadset.add("8");
        listForHeadset.add("pin");
        listForHeadset.add("8-pin");
        listForHeadset.add("type-c");
        listForHeadset.add("3,5");
        listForHeadset.add("3.5");


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
        listForTypeGlass.add("Super-D");
        listForTypeGlass.add("EYE");
        listForTypeGlass.add("Privacy");

        //listForCharging.add("держателем");
        //listForCharging.add("для");
        //listForCharging.add("авто");
        listForCharging.add("микро");
        listForCharging.add("Micro");
        listForCharging.add("micro");
        listForCharging.add("Apple");
        listForCharging.add("8");
        listForCharging.add("pin");
        listForCharging.add("lightning");
        listForCharging.add("Type-C");
        listForCharging.add("type-C");
        listForCharging.add("Type");
        listForCharging.add("type");
        listForCharging.add("C");

        listForBugs.add("(S)");
        listForBugs.add("(L)");
        listForBugs.add("13");
        listForBugs.add("16");
        listForBugs.add("5,5");
        listForBugs.add("дюймов");

        listForBrands.add("HOCO");
        listForBrands.add("Usams");
        listForBrands.add("Baseus");
        listForBrands.add("Borofone");
        listForBrands.add("Celebrat");
        listForBrands.add("Dasimei");
        listForBrands.add("Mietubl");
        listForBrands.add("Xivi");
        listForBrands.add("AIQURA");

        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_1);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_2);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_3);
        //listForCategoryBy_1C.add(PRODUCT_TYPE_1C_4);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_5);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_6);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_7);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_8);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_9);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_10);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_11);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_12);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_13);
        //listForCategoryBy_1C.add(PRODUCT_TYPE_1C_14);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_15);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_16);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_17);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_18);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_19);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_20);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_21);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_22);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_23);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_24);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_25);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_26);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_27);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_28);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_29);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_30);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_31);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_32);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_33);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_34);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_35);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_36);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_37);
        //listForCategoryBy_1C.add(PRODUCT_TYPE_1C_38);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_39);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_40);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_41);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_42);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_43);
        //listForCategoryBy_1C.add(PRODUCT_TYPE_1C_44);
        //listForCategoryBy_1C.add(PRODUCT_TYPE_1C_45);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_46);
        //listForCategoryBy_1C.add(PRODUCT_TYPE_1C_47);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_48);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_49);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_50);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_51);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_52);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_53);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_54);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_55);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_56);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_57);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_58);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_59);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_60);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_61);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_62);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_63);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_64);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_65);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_66);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_67);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_68);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_69);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_70);
        //listForCategoryBy_1C.add(PRODUCT_TYPE_1C_71);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_72);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_73);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_74);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_75);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_76);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_77);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_78);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_79);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_83);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_84);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_85);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_86);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_87);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_88);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_89);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_90);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_91);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_92);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_93);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_94);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_96);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_97);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_98);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_99);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_100);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_101);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_102);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_103);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_104);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_105);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_106);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_107);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_108);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_109);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_110);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_111);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_112);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_113);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_114);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_115);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_116);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_117);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_118);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_119);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_120);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_121);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_122);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_123);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_124);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_125);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_126);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_127);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_128);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_130);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_131);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_132);
        //listForCategoryBy_1C.add(PRODUCT_TYPE_1C_133);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_134);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_135);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_136);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_137);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_138);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_139);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_140);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_142);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_143);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_144);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_145);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_146);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_147);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_148);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_149);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_150);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_151);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_152);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_153);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_154);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_155);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_156);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_157);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_158);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_159);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_160);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_161);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_162);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_163);
        listForCategoryBy_1C.add(PRODUCT_TYPE_1C_164);
    }
}
