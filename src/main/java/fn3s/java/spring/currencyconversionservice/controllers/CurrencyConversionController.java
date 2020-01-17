package fn3s.java.spring.currencyconversionservice.controllers;

import fn3s.java.spring.currencyconversionservice.bean.CurrencyConversionBean;
import fn3s.java.spring.currencyconversionservice.config.Configuration;
import fn3s.java.spring.currencyconversionservice.proxy.CurrencyExchangeServiceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CurrencyConversionController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Configuration config;

    @Autowired
    private CurrencyExchangeServiceProxy proxy;

    @GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean getCurrencyConversion(
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable BigDecimal quantity
    )
    {
        String urlExchange = "http://localhost:8000/currency-exchange/from/{from}/to/{to}";
        Map<String, String> uriVariable = new HashMap<>();
        uriVariable.put("from", from);
        uriVariable.put("to", to);
        ResponseEntity<CurrencyConversionBean> forEntity = new RestTemplate().getForEntity(urlExchange, CurrencyConversionBean.class, uriVariable);
        CurrencyConversionBean response = forEntity.getBody();
        log.info("this CurrencyConversionController@getCurrencyConversionFeign Service: from = {}, to = {}. Name of App: {}", from, to, config.getName());
        return new CurrencyConversionBean(response.getId(), response.getFrom(), response.getTo(), response.getConversionMultiple(), quantity, quantity.multiply(response.getConversionMultiple()), response.getPort());
    }

    @GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean getCurrencyConversionFeign(
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable BigDecimal quantity
    )
    {
        log.info("this CurrencyConversionController@getCurrencyConversionFeign Service: from = {}, to = {}. Name of App: {}", from, to, config.getName());
        CurrencyConversionBean response = proxy.retrieveExchangeValue(from, to);

        return new CurrencyConversionBean(response.getId(), response.getFrom(), response.getTo(), response.getConversionMultiple(), quantity, quantity.multiply(response.getConversionMultiple()), response.getPort());
    }
}
