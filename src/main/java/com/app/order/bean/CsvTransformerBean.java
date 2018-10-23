package com.app.order.bean;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
@Transactional
@ApplicationScoped
@Named
public class CsvTransformerBean {

    @Inject
    private CSVUtilBean csvUtilBean;

    @PostConstruct
    private void init() {
        try {

            csvUtilBean.process(new File(this.getClass().getResource("/inputFile.csv").toURI()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
