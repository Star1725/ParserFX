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

    //статика для проверки заговков столбцов файла отчёта Wildberies
    public static final String BRAND_NAME_IN_FILE_WILDBERIES = "Бренд";//0-й столбец
    public static final String CATEGORY_NAME_IN_FILE_WILDBERIES = "Предмет";//1-й столбец
    public static final String CODE_1C_IN_FILE_WILDBERIES = "Артикул поставщика";//3-й столбец
    public static final String VENDOR_CODE_IN_FILE_WILDBERIES = "Номенклатура (код 1С)";//4-й столбец
    public static final String VENDOR_CODE_1C_IN_FILE_WILDBERIES = "Последний баркод";//5-й столбец
    public static final String PRICE_U_IN_FILE_WILDBERIES = "Текущая розн. цена (до скидки)";//11-й столбец
    public static final String BASIC_SALE_IN_FILE_WILDBERIES = "Текущая скидка на сайте, %";//13-й столбец
    public static final String PROMO_SALE_IN_FILE_WILDBERIES = "Текущая скидка по промокоду, %";//16-й столбец

    //статика для проверки заговков столбцов файла отчёта 1С по Wildberies
    public static final String CODE_1C = "Код";//0-й столбец
    public static final String VENDOR_CODE_1C = "Артикул";//1-й столбец
    public static final String SPEC_PRICE_1C = "Цена";//4-й столбец

    //статика для проверки заговков столбцов файла отчёта Ozon
    public static final String VENDOR_CODE_1C_IN_FILE_OZON = "Артикул";//0-й столбец
    public static final String VENDOR_CODE_IN_FILE_OZON = "Ozon SKU ID";//1-й столбец
    public static final String BRAND_AND_PRODUCT_NAME_IN_FILE_OZON = "Название";//2-й столбец
    public static final String PRICE_U_IN_FILE_OZON = "Цена до скидки, руб.";//16-й столбец
    public static final String BASIC_PRICE_IN_FILE_OZON = "Текущая цена (со скидкой), руб.";//17-й столбец
    public static final String PROMO_PRICE_IN_FILE_OZON = "Цена с учетом акции, руб.";//20-й столбец
    public static final String PREMIUM_PRICE_IN_FILE_OZON = "Цена с Ozon Premium, руб.";//23-й столбец

    public static final ArrayList<String> listForTypeConnect = new ArrayList<>();
    public static final ArrayList<String> listForTypeGlass = new ArrayList<>();
    public static final ArrayList<String> listForCabel = new ArrayList<>();
    public static final ArrayList<String> listForBrands = new ArrayList<>();
    public static final ArrayList<String> listForMasks = new ArrayList<>();
    public static final ArrayList<String> listForDescriptionsOzon = new ArrayList<>();

    static {
        listForTypeConnect.add("Type-C");
        listForTypeConnect.add("Type-C(f)");
        listForTypeConnect.add("Apple");
        listForTypeConnect.add("8-pin");
        listForTypeConnect.add("pin(f)");
        listForTypeConnect.add("8");
        listForTypeConnect.add("pin");
        listForTypeConnect.add("микро");
        listForTypeConnect.add("угловой");
        listForTypeConnect.add("lightning");
        listForTypeConnect.add("2-in-");
        listForTypeConnect.add("3-in-");
        listForTypeConnect.add("4-in-");
        listForTypeConnect.add("USB(f)");

        listForTypeGlass.add("EYE PROTECTION");
        listForTypeGlass.add("5D");
        listForTypeGlass.add("11D");
        listForTypeGlass.add("2.5D");
        listForTypeGlass.add("SuperD");
        listForTypeGlass.add("SUPER-D");
        listForTypeGlass.add("EYE");
        listForTypeGlass.add("Privacy");

        listForCabel.add("1.0м");
        listForCabel.add("1.2м");
        listForCabel.add("1.5м");
        listForCabel.add("1.8м");
        listForCabel.add("2.0м");
        listForCabel.add("3.0м");
        listForCabel.add("5.0м");

        listForBrands.add("HOCO");
        listForBrands.add("Usams");
        listForBrands.add("Baseus");
        listForBrands.add("Borofone");
        listForBrands.add("Celebrat");
        listForBrands.add("Dasimei");

        listForMasks.add("одноразовая");
        listForMasks.add("трёхслойная");

        listForDescriptionsOzon.add("FM-трансмиттер");
        listForDescriptionsOzon.add("USB- хаб");
        listForDescriptionsOzon.add("USB-хаб");
        listForDescriptionsOzon.add("USB-хвб");
        listForDescriptionsOzon.add("Автомобильная корзина для мусора");
        listForDescriptionsOzon.add("Автомобильный контейнер для мусора");
        listForDescriptionsOzon.add("Автомобильная пепельница");
        listForDescriptionsOzon.add("Автомобильная рулонная штора");
        listForDescriptionsOzon.add("Автомобильное зарядное устройство");
        listForDescriptionsOzon.add("Автомобильное полотенце");
        listForDescriptionsOzon.add("Автомобильный Bluetooth-приемник");
        listForDescriptionsOzon.add("Автомобильный держатель");
        listForDescriptionsOzon.add("Автомобильный очиститель воздуха");
        listForDescriptionsOzon.add("Автомобильный Пылесос");
        listForDescriptionsOzon.add("Автомобильный светильник");
        listForDescriptionsOzon.add("Автомобильный стеклоочиститель");
        listForDescriptionsOzon.add("Автомойка");
        listForDescriptionsOzon.add("Адаптер");
        listForDescriptionsOzon.add("Адаптер-переходник");
        listForDescriptionsOzon.add("Аккумулятор внешний");
        listForDescriptionsOzon.add("Ароматизатор автомобильный");
        listForDescriptionsOzon.add("Аудио конвертер Lightning в двойной Lightning");
        listForDescriptionsOzon.add("Аудио-адаптер");
        listForDescriptionsOzon.add("Аудио-переходник");
        listForDescriptionsOzon.add("Бесконтактный диспенсер-спрей для рук");
        listForDescriptionsOzon.add("Беспроводная колонка");
        listForDescriptionsOzon.add("Беспроводное зарядное устройство");
        listForDescriptionsOzon.add("Беспроводной ресивер");
        listForDescriptionsOzon.add("Блок питания");
        listForDescriptionsOzon.add("Велосипедный держатель для телефона");
        listForDescriptionsOzon.add("Гарнитура");
        listForDescriptionsOzon.add("Гарнитуры");
        listForDescriptionsOzon.add("Держатель");
        listForDescriptionsOzon.add("Дозатор для жидкого мыла");
        listForDescriptionsOzon.add("Док-станция");
        listForDescriptionsOzon.add("Зарядная подставка");
        listForDescriptionsOzon.add("Защитный чехол");
        listForDescriptionsOzon.add("Кабель");
        listForDescriptionsOzon.add("Кабель-переходник");
        listForDescriptionsOzon.add("Картридер");
        listForDescriptionsOzon.add("Колонка");
        listForDescriptionsOzon.add("Кольцевая LED-лампа для селфи");// -> лампа
        listForDescriptionsOzon.add("Магнитный  кабель");
        listForDescriptionsOzon.add("Маска");
        listForDescriptionsOzon.add("Мини-холодильник");
        listForDescriptionsOzon.add("Мини-штатив");
        listForDescriptionsOzon.add("Многофункциональный магнитный держатель с крючком");
        listForDescriptionsOzon.add("Многофункциональный стерилизатор");
        listForDescriptionsOzon.add("Монопод для селфи");
        listForDescriptionsOzon.add("Накладка для телефона");
        listForDescriptionsOzon.add("Накладка задняя");
        listForDescriptionsOzon.add("Насадка МОП");
        listForDescriptionsOzon.add("Настольная лампа");
        listForDescriptionsOzon.add("Настольная подставка");
        listForDescriptionsOzon.add("Наушники внутриканальные");
        listForDescriptionsOzon.add("Наушники полноразмерные");
        listForDescriptionsOzon.add("Ночник");
        listForDescriptionsOzon.add("Органайзер для проводов и аксессуаров");
        listForDescriptionsOzon.add("Переходник");
        listForDescriptionsOzon.add("Подсветка экрана");
        listForDescriptionsOzon.add("Подставка под кольцо для телефона");
        listForDescriptionsOzon.add("Подстаканник для подогрева и охлаждения напитков в автомобиле");
        listForDescriptionsOzon.add("Подушка для шеи");
        listForDescriptionsOzon.add("Портативный увлажнитель воздуха");
        listForDescriptionsOzon.add("Пульт дистанционного управления");
        listForDescriptionsOzon.add("Пылесос автомобильный");
        listForDescriptionsOzon.add("Разветвитель HDMI");
        listForDescriptionsOzon.add("Ремешок Baseus для умных часов");
        listForDescriptionsOzon.add("Ручная сумка-чехол для Nintendo Switch");
        listForDescriptionsOzon.add("Рюкзак для ноутбука");
        listForDescriptionsOzon.add("Светильник");
        listForDescriptionsOzon.add("Селфи палка-штатив");
        listForDescriptionsOzon.add("Сетевое зарядное устройство");
        listForDescriptionsOzon.add("Сетевой разветвитель для кабеля HDMI");
        listForDescriptionsOzon.add("Силиконовый чехол");
        listForDescriptionsOzon.add("Стилус");
        listForDescriptionsOzon.add("Сумка для мусора");
        listForDescriptionsOzon.add("Сумка для ноутбука");
        listForDescriptionsOzon.add("Сумка для хранения");
        listForDescriptionsOzon.add("Съемная линза для камеры");
        listForDescriptionsOzon.add("Термометр бесконтактный инфракрасный");
        listForDescriptionsOzon.add("Увлажнитель воздуха");
        listForDescriptionsOzon.add("Ультрафиолетовая лампа");
        listForDescriptionsOzon.add("Ультрафиолетовый стерилизатор");
        listForDescriptionsOzon.add("Усилитель сигнала HDMI");
        listForDescriptionsOzon.add("Устройство зарядное беспроводное");
        listForDescriptionsOzon.add("Фильтр для очистителя воздуха");
        listForDescriptionsOzon.add("Фильтр для пылесоса");
        listForDescriptionsOzon.add("Чехол для беспроводной зарядки");
        listForDescriptionsOzon.add("Чехол для ноутбука");
        listForDescriptionsOzon.add("Чехол силиконовый");
        listForDescriptionsOzon.add("Чехол-книжка");
        listForDescriptionsOzon.add("Щетка для автомобиля");
        listForDescriptionsOzon.add("Электрическая мухобойка");
    }
}
