package ru.cherkovskiy;

import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Generator {

    private static final String bodyStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<FIXML_BATCH>\n" +
            "  <FIXML>\n" +
            "    <BATCH>\n" +
            "      <Hdr TrgtCompId=\"ASUOVP-01\" SndCompId=\"APAM2\" TID=\"GAVR\" SID=\"EFX01\" MsgID=\"067e61623b6f4ae2a1712470b63dff00\" SeqNum=\"1\" SendingTime=\"20170226-08:44:27.498\"/>\n" +
            "      <ExecRpt Acct=\"SBMB\" Txt=\"filled 1000000@59.5598\" OrdID=\"3432434343\" ClOrdID=\"1458204927700\" ID=\"6449013711\" OrdStat=\"2\" TxnTm=\"20170226-08:44:27.498\" LastPx=\"59.5598\" PeggedPx=\"59.5398\" LastQty=\"1000000.0\" PxTyp=\"2\" Side=\"1\" ExecID=\"1234512345\"> \n" +
            "        <Instrmt Sym=\"USD/RUB\" Prod=\"4\" Mult='1'/>  \n" +
            "      </ExecRpt>\n" +
            "    </BATCH>\n" +
            "  </FIXML>\n" +
            "</FIXML_BATCH>";

    public static void main(String[] args) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        int i = 0;
        while(true) {
            try {
                restTemplate.postForLocation("http://localhost:8888/efx", bodyStr);
            } catch (Exception ex){}
            System.out.println(++i);
            TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(100, 500));
        }
    }
}
