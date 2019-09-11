# project# README


Для подключения к БД необходимо в классе App изменить переменные user и password

	public static String user = "postgres";
	public static String password = "password";

#### Методы

+ strCheck: Проверка строки на валидность
    + Вход - строка
    + Выход - true - если строка валидна, false - если нет
    
+ dateCheck: Проверка даты на валидность(мм-дд-гг)
    + Вход - строка
    + Выход - true - если строка валидна, false - если нет
+ isNumber: Проверка строки на число
    + Вход - строка
    + Выход - true - если строка является числом, false - если нет
+ query: Метод записывающий данные в БД
    + Вход - Массив строк (содержащих данные о новом продукте или данные о покупке или продаже)
    + Выход - true - если данные записаны, false - если нет
+ salesreport: Метод расчитывающий сумму закупок или продаж для продукта
    + Вход - Массив строк, строка(данные о продукте, продажа или покупка)
    + Выход - число - общая сумма закупки или продажи на указанную дату
     
    
#### Список команд
- NEWPRODUCT <имя продукта>
- PURCHASE <имя продукта><количество><цена за единицу><дата>
- DEMAND <имя продукта><количество><цена за единицу><дата>
- SALESREPORT <имя продукта><дата>

#### Пример обработки команд
- Вход:  NEWPRODUCT iphone
- Выход:Ок
- Вход:  NEWPRODUCT iphone
- Выход:Error
- Вход:  PURCHASE iphone 1 1000 01.01.2017
- Выход:Ок
- Вход:  PURCHASE iphone 2 2000 01.02.2017
- Выход:Ок
- Вход: DEMAND iphone 2 5000 01.03.2017
- Выход:Ок
- Вход: SALESREPORT iphone 02.03.2017
- Выход:7000

## Данные для БД

#### products
                    
id | product
------------- | -------------
1  | product1
2  | product2 

- id - SERIAL PRIMARY KEY 
- product - VARCHAR(30)
#### purchase
                    
id  | products_id| purchase_amount | purchase_price | purchase_date
------ | -----------|---------|------|-------
1  | 4 | 1  | 1500  | 12-12-2000  |
- id - SERIAL PRIMARY KEY 
- products_id - INT NOT NULL FOREIGN KEY REFERENCES products (id)
- purchase_amount -  INT NOT NULL
- purchase_price - INT NOT NULL
- purchase_date - DATE NOT NULL

#### demand
id  | products_id| demand_amount | demand_price | demand_date
------ | -----------|---------|------|-------
1  | 4 | 1  | 1500  | 12-12-2000  |
- id - SERIAL PRIMARY KEY 
- products_id - INT NOT NULL FOREIGN KEY REFERENCES products (id)
- demand_amount -  INT NOT NULL
- demand_price - INT NOT NULL
- demand_date - DATE NOT NULL
