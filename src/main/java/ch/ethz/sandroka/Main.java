package ch.ethz.sandroka;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void argfail() {
        System.err.println("Wrong arguments. Usage: proxmox-client.jar -h host -p port -u username -s password -r realm (e.g. pam) -c path/to/remote-viewer.exe -n node-name -v vm-id");
        System.exit(-1);
    }

    public static void main(String[] args) throws JSONException, IOException {
        // Learned here: https://stackoverflow.com/questions/7341683/parsing-arguments-to-a-java-command-line-program
        final Map<String, List<String>> params = new HashMap<>();
        List<String> options = null;
        for (int i = 0; i < args.length; i++) {
            final String a = args[i];

            if (a.charAt(0) == '-') {
                if (a.length() < 2) {
                    System.err.println("Error at argument " + a);
                    return;
                }

                options = new ArrayList<>();
                params.put(a.substring(1), options);
            } else if (options != null) {
                options.add(a);
            } else {
                System.err.println("Illegal parameter usage");
                return;
            }
        }
        if (params.get("h") == null) argfail();
        if (params.get("p") == null) argfail();
        if (params.get("u") == null) argfail();
        if (params.get("s") == null) argfail();
        if (params.get("r") == null) argfail();
        if (params.get("c") == null) argfail();
        if (params.get("n") == null) argfail();
        if (params.get("v") == null) argfail();

        String proxhost = params.get("h").get(0);
        int proxport = Integer.parseInt(params.get("p").get(0));
        String proxuser = params.get("u").get(0);
        String proxpw = params.get("s").get(0);
        String proxrealm = params.get("r").get(0);
        String proxclient = params.get("c").get(0);
        String proxnode = params.get("n").get(0);
        int proxvm = Integer.parseInt(params.get("v").get(0));

        File f = File.createTempFile("remote-viewer", ".vv");

        Client client = new Client(proxhost, proxport);
        client.login(proxuser, proxpw, proxrealm);

        JSONObject spice = client.getNodes().get(proxnode).getQemu().get(proxvm).getSpiceproxy().spiceproxy().getResponse().getJSONObject("data");

        String attention = spice.getString("secure-attention");
        String delete = spice.getString("delete-this-file");
        String proxy = spice.getString("proxy");
        String type = spice.getString("type");
        String ca = spice.getString("ca");
        String fullscreen = spice.getString("toggle-fullscreen");
        String title = spice.getString("title");
        String host = spice.getString("host");
        String pw = spice.getString("password");
        String subject = spice.getString("host-subject");
        String cursor = spice.getString("release-cursor");
        String port = spice.getString("tls-port");


        // Inspired from https://forum.proxmox.com/threads/remote-spice-access-without-using-web-manager.16561/page-2

        PrintWriter writer = new PrintWriter(f, "UTF-8");
        writer.println("[virt-viewer]");
        writer.println("secure-attention=" + attention);
        writer.println("delete-this-file=" + delete);
        writer.println("proxy=" + proxy);
        writer.println("type=" + type);
        writer.println("ca=" + ca);
        writer.println("toggle-fullscreen=" + fullscreen);
        writer.println("title=" + title);
        writer.println("host=" + host);
        writer.println("password=" + pw);
        writer.println("host-subject=" + subject);
        writer.println("release-cursor=" + cursor);
        writer.println("tls-port=" + port);
        writer.close();

        try {
            String line;
            Process p = new ProcessBuilder(proxclient, f.getAbsolutePath()).start();

            BufferedReader input =
                    new BufferedReader
                            (new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
            input.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}
