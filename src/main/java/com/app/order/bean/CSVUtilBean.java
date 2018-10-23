package com.app.order.bean;


import com.app.order.data.Order;
import com.app.order.data.OrderItem;
import com.app.order.data.Stat;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Felhasznált: https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
@Transactional
@ApplicationScoped
@Named
public class CSVUtilBean {

    @PersistenceContext
    private EntityManager em;

    private static final char DEFAULT_SEPARATOR = ';';
    private static final char DEFAULT_QUOTE = '"';
    private static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final String NUMBER_REGEX = "[0-9]+";


    public void process(File file) throws Exception {

        File out = new File("src\\main\\resources\\responseFile.csv");
        out.createNewFile();
        FileWriter writer = new FileWriter(out);

        HashSet<Long> errorIds = new HashSet<>();
        Scanner scanner = new Scanner(file);
        int i = 0;

        List<Order> orderList = new ArrayList<>();
        List<OrderItem> orderItemList = new ArrayList<>();

        while (scanner.hasNext()) {
            List<String> line = parseLine(scanner.nextLine());

            if (i > 0) {
                Order order = new Order();
                order.setOrderId(Long.parseLong(line.get(2)));
                order.setBuyerName(line.get(3));

                if (validate(line.get(4))) {
                    order.setBuyerEmail(line.get(4));

                } else {
                    writer.write(line.get(0));
                    writer.write(',');
                    writer.write("Status: Error. Email is not correct, Please check it!");
                    writer.write('\n');
                    errorIds.add(Long.parseLong(line.get(2)));
                }
                order.setAddress(line.get(5));

                if (line.get(6).matches(NUMBER_REGEX)) {
                    order.setPostcode(Integer.parseInt(line.get(6)));
                } else {
                    writer.write(line.get(0));
                    writer.write(',');
                    writer.write("Status: Error. Postcode is not correct, Please check it!");
                    writer.write('\n');
                    errorIds.add(Long.parseLong(line.get(2)));
                }

                if (line.get(11).equals("") || line.get(11).matches(DATE_PATTERN)) {
                    if (line.get(11).equals("")) {
                        order.setOrderDate(new Date());
                    } else {
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        Date date = format.parse(line.get(11));
                        order.setOrderDate(date);
                    }
                } else {
                    writer.write(line.get(0));
                    writer.write(',');
                    writer.write("Status: Error. Date is not correct, Please check it!");
                    writer.write('\n');
                    errorIds.add(Long.parseLong(line.get(2)));
                }
                if (!errorIds.contains(order.getOrderId())) {
                    writer.write(line.get(0));
                    writer.write(',');
                    writer.write("ok!");
                    writer.write('\n');

                }
                orderList.add(order);


                OrderItem orderItem = new OrderItem();
                orderItem.setOrderItemId(Long.parseLong(line.get(1)));
                orderItem.setOrderId(Long.parseLong(line.get(2)));
                orderItem.setSalePrice(Double.parseDouble(line.get(7)));
                orderItem.setShippingPrice(Double.parseDouble(line.get(8)));
                orderItem.setSku(line.get(9));
                orderItem.setTotalItemPrice(orderItem.getSalePrice() + orderItem.getShippingPrice());
                orderItem.setStatus(Stat.valueOf(line.get(10)));
                orderItemList.add(orderItem);
                em.merge(orderItem);
            }
            i++;
        }
        scanner.close();


        for (Order order : orderList) {
            // akkor fut le ha nem fut hibára egyszersem
            if (!errorIds.contains(order.getOrderId())) {
                for (OrderItem orderItem : orderItemList) {
                    //megnézzük ua az e az itemnek az id mint az ordernek
                    if (order.getOrderId().equals(orderItem.getOrderId())) {
                        if (order.getOrderTotalValue() == null) {
                            order.setOrderTotalValue(orderItem.getTotalItemPrice());
                        } else {
                            order.setOrderTotalValue(order.getOrderTotalValue() + orderItem.getTotalItemPrice());
                        }
                    }
                }
                em.merge(order);
            }
        }
        //kiiratjuk fájlba
        writer.close();
    }

    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators) {
        return parseLine(cvsLine, separators, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<>();

        //if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());

        return result;
    }

    // https://stackoverflow.com/questions/8204680/java-regex-email
    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
}
