package com.footix.backend.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.springframework.stereotype.Component;

@Component
public class SquadProvider {

    public record PlayerSeed(String name, String position, int number, boolean starter, int age) {}
    public record SquadTemplate(String formation, List<PlayerSeed> players) {}

    private static final Map<String, String> FORMATIONS = Map.ofEntries(
            Map.entry("Spain", "4-3-3"), Map.entry("France", "4-2-3-1"), Map.entry("England", "4-3-3"),
            Map.entry("Brazil", "4-2-3-1"), Map.entry("Argentina", "4-4-2"), Map.entry("Germany", "4-2-3-1"),
            Map.entry("Portugal", "4-3-3"), Map.entry("Netherlands", "4-3-3"), Map.entry("Belgium", "3-4-2-1"),
            Map.entry("Mexico", "4-3-3"), Map.entry("United States", "4-3-3"), Map.entry("Canada", "4-4-2")
    );

    private static final Map<String, String[][]> STARTERS = buildStarters();

    public SquadTemplate getSquad(String teamName, String countryCode) {
        String formation = FORMATIONS.getOrDefault(teamName, pickFormation(teamName));
        List<PlayerSeed> players = new ArrayList<>();
        Random rng = new Random(Objects.hash(teamName, countryCode));

        String[][] starters = STARTERS.getOrDefault(teamName, generateStarters(teamName, countryCode, rng));
        for (String[] s : starters) {
            players.add(new PlayerSeed(s[0], s[1], Integer.parseInt(s[2]), true, Integer.parseInt(s[3])));
        }

        String[] firstNames = firstNamesFor(countryCode);
        String[] lastNames = lastNamesFor(countryCode);
        Set<String> used = new HashSet<>();
        players.forEach(p -> used.add(p.name()));

        int num = 12;
        while (players.size() < 26) {
            String name = firstNames[rng.nextInt(firstNames.length)] + " " + lastNames[rng.nextInt(lastNames.length)];
            if (!used.add(name)) continue;
            String pos = benchPosition(players.size());
            players.add(new PlayerSeed(name, pos, num++, false, 20 + rng.nextInt(15)));
        }
        return new SquadTemplate(formation, players);
    }

    private String benchPosition(int squadSize) {
        int benchIdx = squadSize - 11;
        if (benchIdx < 2) return "GK";
        if (benchIdx < 5) return "DF";
        if (benchIdx < 9) return "MF";
        return "FW";
    }

    public static String photoUrl(String playerName) {
        String encoded = URLEncoder.encode(playerName, StandardCharsets.UTF_8);
        return "https://ui-avatars.com/api/?name=" + encoded + "&size=256&background=1a2d4a&color=00d4aa&bold=true&format=png";
    }

    private String pickFormation(String team) {
        String[] formations = {"4-3-3", "4-4-2", "4-2-3-1", "3-5-2"};
        return formations[Math.abs(team.hashCode()) % formations.length];
    }

    private String[][] generateStarters(String team, String code, Random rng) {
        String[] fn = firstNamesFor(code);
        String[] ln = lastNamesFor(code);
        String[][] result = new String[11][4];
        String[] positions = {"GK", "DF", "DF", "DF", "DF", "MF", "MF", "MF", "FW", "FW", "FW"};
        int[] numbers = {1, 2, 3, 4, 5, 6, 8, 10, 7, 9, 11};
        for (int i = 0; i < 11; i++) {
            result[i] = new String[]{fn[rng.nextInt(fn.length)] + " " + ln[rng.nextInt(ln.length)],
                    positions[i], String.valueOf(numbers[i]), String.valueOf(22 + rng.nextInt(10))};
        }
        return result;
    }

    private String[] firstNamesFor(String code) {
        return switch (code != null ? code : "XX") {
            case "MX" -> new String[]{"Carlos", "Diego", "Luis", "Miguel", "Jorge", "Ricardo", "Fernando", "Alejandro"};
            case "FR" -> new String[]{"Antoine", "Kylian", "Olivier", "Adrien", "Jules", "William", "Aurélien", "Dayot"};
            case "BR" -> new String[]{"Vinícius", "Rodrygo", "Raphinha", "Bruno", "Casemiro", "Marquinhos", "Alisson", "Gabriel"};
            case "AR" -> new String[]{"Lionel", "Ángel", "Lautaro", "Julián", "Enzo", "Alexis", "Cristian", "Nahuel"};
            case "DE" -> new String[]{"Manuel", "Joshua", "Jamal", "Florian", "Kai", "Leroy", "Antonio", "Niklas"};
            case "ES" -> new String[]{"Pedri", "Gavi", "Álvaro", "Dani", "Ferran", "Nico", "Mikel", "Aymeric"};
            case "GB", "EN" -> new String[]{"Harry", "Jude", "Bukayo", "Phil", "Declan", "John", "Kyle", "Marcus"};
            case "US" -> new String[]{"Christian", "Tyler", "Weston", "Giovanni", "Tim", "Sergiño", "Antonee", "Haji"};
            case "CA" -> new String[]{"Alphonso", "Jonathan", "Cyle", "Sam", "Stephen", "Richie", "Liam", "Jacob"};
            case "PT" -> new String[]{"Cristiano", "Bruno", "Bernardo", "Rúben", "João", "Diogo", "Rafael", "Nuno"};
            case "NL" -> new String[]{"Virgil", "Frenkie", "Memphis", "Cody", "Steven", "Daley", "Jeremie", "Xavi"};
            case "BE" -> new String[]{"Kevin", "Romelu", "Thibaut", "Youri", "Jan", "Timothy", "Arthur", "Amadou"};
            case "JP" -> new String[]{"Takefusa", "Kaoru", "Wataru", "Hiroki", "Daizen", "Ritsu", "Junya", "Ayase"};
            case "KR" -> new String[]{"Son", "Kim", "Lee", "Hwang", "Cho", "Jung", "Paik", "Hong"};
            case "MA" -> new String[]{"Achraf", "Youssef", "Sofyan", "Noussair", "Azzedine", "Brahim", "Romain", "Walid"};
            case "SN" -> new String[]{"Sadio", "Kalidou", "Édouard", "Idrissa", "Ismaïla", "Nicolas", "Pape", "Moussa"};
            default -> new String[]{"Alex", "Marco", "Lucas", "Daniel", "Thomas", "James", "David", "Michael"};
        };
    }

    private String[] lastNamesFor(String code) {
        return switch (code != null ? code : "XX") {
            case "MX" -> new String[]{"Hernández", "Lozano", "Jiménez", "Álvarez", "Vega", "Romo", "Montes", "Sánchez"};
            case "FR" -> new String[]{"Mbappé", "Giroud", "Rabiot", "Koundé", "Saliba", "Tchouaméni", "Dembélé", "Coman"};
            case "BR" -> new String[]{"Júnior", "Paquetá", "Silva", "Santos", "Martinelli", "Rodrigues", "Becker", "Magalhães"};
            case "AR" -> new String[]{"Messi", "Di María", "Martínez", "Álvarez", "Fernández", "Mac Allister", "Romero", "Molina"};
            case "DE" -> new String[]{"Neuer", "Kimmich", "Musiala", "Wirtz", "Havertz", "Sané", "Rüdiger", "Süle"};
            case "ES" -> new String[]{"Morata", "Carvajal", "Laporte", "Olmo", "Torres", "Williams", "Oyarzabal", "Ruiz"};
            case "GB", "EN" -> new String[]{"Kane", "Bellingham", "Saka", "Foden", "Rice", "Stones", "Walker", "Rashford"};
            case "US" -> new String[]{"Pulisic", "Adams", "McKennie", "Reyna", "Weah", "Dest", "Robinson", "Wright"};
            case "CA" -> new String[]{"Davies", "David", "Larin", "Adekugbe", "Eustáquio", "Laryea", "Millar", "Shaffelburg"};
            case "PT" -> new String[]{"Ronaldo", "Fernandes", "Silva", "Dias", "Félix", "Jota", "Leão", "Mendes"};
            case "NL" -> new String[]{"van Dijk", "de Jong", "Depay", "Gakpo", "Bergwijn", "Blind", "Frimpong", "Simons"};
            case "BE" -> new String[]{"De Bruyne", "Lukaku", "Courtois", "Tielemans", "Vertonghen", "Castagne", "Theate", "Onana"};
            case "JP" -> new String[]{"Kubo", "Mitoma", "Endo", "Ito", "Maeda", "Doan", "Itakura", "Kamada"};
            case "KR" -> new String[]{"Heung-min", "Min-jae", "Kang-in", "Hee-chan", "Geun-ho", "Woo-young", "Seung-ho", "Soo-hyun"};
            case "MA" -> new String[]{"Hakimi", "En-Nesyri", "Amrabat", "Mazraoui", "Ounahi", "Ziyech", "Saïss", "Aguerd"};
            case "SN" -> new String[]{"Mané", "Koulibaly", "Mendy", "Gueye", "Sarr", "Jackson", "Gueye", "Ndiaye"};
            default -> new String[]{"Silva", "Santos", "García", "Müller", "Rossi", "Martin", "Bernard", "Dupont"};
        };
    }

    private static Map<String, String[][]> buildStarters() {
        Map<String, String[][]> m = new HashMap<>();
        m.put("Mexico", arr(
                "Guillermo Ochoa", "GK", "1", "37",
                "Jorge Sánchez", "DF", "2", "27",
                "César Montes", "DF", "3", "27",
                "Héctor Moreno", "DF", "4", "36",
                "Gerardo Arteaga", "DF", "5", "25",
                "Edson Álvarez", "MF", "6", "26",
                "Luis Romo", "MF", "8", "28",
                "Hirving Lozano", "FW", "10", "28",
                "Raúl Jiménez", "FW", "9", "33",
                "Alexis Vega", "FW", "11", "26",
                "Orbelín Pineda", "MF", "7", "28"));
        m.put("France", arr(
                "Mike Maignan", "GK", "1", "28",
                "Jules Koundé", "DF", "2", "25",
                "William Saliba", "DF", "3", "23",
                "Dayot Upamecano", "DF", "4", "25",
                "Théo Hernandez", "DF", "5", "26",
                "Aurélien Tchouaméni", "MF", "6", "24",
                "Adrien Rabiot", "MF", "8", "29",
                "Antoine Griezmann", "MF", "7", "33",
                "Kylian Mbappé", "FW", "10", "27",
                "Olivier Giroud", "FW", "9", "37",
                "Ousmane Dembélé", "FW", "11", "27"));
        m.put("Brazil", arr(
                "Alisson Becker", "GK", "1", "31",
                "Danilo", "DF", "2", "32",
                "Marquinhos", "DF", "3", "29",
                "Gabriel Magalhães", "DF", "4", "26",
                "Wendell", "DF", "5", "30",
                "Bruno Guimarães", "MF", "6", "26",
                "Casemiro", "MF", "8", "32",
                "Rodrygo", "FW", "10", "23",
                "Vinícius Júnior", "FW", "7", "24",
                "Richarlison", "FW", "9", "27",
                "Raphinha", "FW", "11", "27"));
        m.put("Argentina", arr(
                "Emiliano Martínez", "GK", "1", "31",
                "Nahuel Molina", "DF", "2", "26",
                "Cristian Romero", "DF", "3", "26",
                "Nicolás Otamendi", "DF", "4", "36",
                "Marcos Acuña", "DF", "5", "32",
                "Enzo Fernández", "MF", "6", "23",
                "Alexis Mac Allister", "MF", "8", "25",
                "Ángel Di María", "FW", "7", "36",
                "Lionel Messi", "FW", "10", "38",
                "Lautaro Martínez", "FW", "9", "27",
                "Julián Álvarez", "FW", "11", "24"));
        m.put("Germany", arr(
                "Manuel Neuer", "GK", "1", "38",
                "Joshua Kimmich", "DF", "2", "29",
                "Antonio Rüdiger", "DF", "3", "31",
                "Jonathan Tah", "DF", "4", "28",
                "David Raum", "DF", "5", "26",
                "Robert Andrich", "MF", "6", "29",
                "Jamal Musiala", "MF", "10", "21",
                "Florian Wirtz", "MF", "7", "21",
                "Kai Havertz", "FW", "9", "25",
                "Leroy Sané", "FW", "11", "28",
                "Niclas Füllkrug", "FW", "8", "31"));
        m.put("Spain", arr(
                "Unai Simón", "GK", "1", "27",
                "Dani Carvajal", "DF", "2", "32",
                "Aymeric Laporte", "DF", "3", "30",
                "Robin Le Normand", "DF", "4", "28",
                "Marc Cucurella", "DF", "5", "26",
                "Rodri", "MF", "6", "28",
                "Pedri", "MF", "8", "21",
                "Nico Williams", "FW", "7", "22",
                "Álvaro Morata", "FW", "9", "31",
                "Lamine Yamal", "FW", "11", "17",
                "Dani Olmo", "MF", "10", "26"));
        m.put("England", arr(
                "Jordan Pickford", "GK", "1", "30",
                "Kyle Walker", "DF", "2", "34",
                "John Stones", "DF", "3", "30",
                "Marc Guéhi", "DF", "4", "24",
                "Luke Shaw", "DF", "5", "28",
                "Declan Rice", "MF", "6", "25",
                "Jude Bellingham", "MF", "8", "21",
                "Phil Foden", "MF", "10", "24",
                "Bukayo Saka", "FW", "7", "22",
                "Harry Kane", "FW", "9", "31",
                "Marcus Rashford", "FW", "11", "26"));
        m.put("United States", arr(
                "Matt Turner", "GK", "1", "30",
                "Sergiño Dest", "DF", "2", "23",
                "Chris Richards", "DF", "3", "24",
                "Walker Zimmerman", "DF", "4", "31",
                "Antonee Robinson", "DF", "5", "26",
                "Tyler Adams", "MF", "6", "25",
                "Weston McKennie", "MF", "8", "25",
                "Giovanni Reyna", "MF", "10", "21",
                "Christian Pulisic", "FW", "7", "25",
                "Tim Weah", "FW", "11", "24",
                "Ricardo Pepi", "FW", "9", "21"));
        m.put("Canada", arr(
                "Milan Borjan", "GK", "1", "36",
                "Richie Laryea", "DF", "2", "29",
                "Steven Vitória", "DF", "3", "37",
                "Derek Cornelius", "DF", "4", "26",
                "Alphonso Davies", "DF", "5", "23",
                "Sam Adekugbe", "DF", "6", "29",
                "Stephen Eustáquio", "MF", "8", "27",
                "Jonathan David", "FW", "9", "24",
                "Cyle Larin", "FW", "10", "29",
                "Liam Millar", "FW", "11", "25",
                "Tajon Buchanan", "MF", "7", "25"));
        m.put("Portugal", arr(
                "Diogo Costa", "GK", "1", "24",
                "João Cancelo", "DF", "2", "30",
                "Rúben Dias", "DF", "3", "27",
                "Pepe", "DF", "4", "41",
                "Nuno Mendes", "DF", "5", "22",
                "João Palhinha", "MF", "6", "28",
                "Bruno Fernandes", "MF", "8", "29",
                "Bernardo Silva", "MF", "10", "29",
                "Cristiano Ronaldo", "FW", "7", "39",
                "Rafael Leão", "FW", "11", "25",
                "Diogo Jota", "FW", "9", "27"));
        m.put("Netherlands", arr(
                "Bart Verbruggen", "GK", "1", "22",
                "Jeremie Frimpong", "DF", "2", "23",
                "Virgil van Dijk", "DF", "3", "33",
                "Nathan Aké", "DF", "4", "29",
                "Daley Blind", "DF", "5", "34",
                "Frenkie de Jong", "MF", "6", "27",
                "Xavi Simons", "MF", "10", "21",
                "Steven Bergwijn", "FW", "7", "26",
                "Memphis Depay", "FW", "9", "30",
                "Cody Gakpo", "FW", "11", "25",
                "Tijjani Reijnders", "MF", "8", "25"));
        m.put("Belgium", arr(
                "Thibaut Courtois", "GK", "1", "32",
                "Timothy Castagne", "DF", "2", "28",
                "Wout Faes", "DF", "3", "26",
                "Arthur Theate", "DF", "4", "24",
                "Zeno Debast", "DF", "5", "21",
                "Amadou Onana", "MF", "6", "23",
                "Kevin De Bruyne", "MF", "7", "33",
                "Youri Tielemans", "MF", "8", "27",
                "Romelu Lukaku", "FW", "9", "31",
                "Jérémy Doku", "FW", "11", "22",
                "Loïs Openda", "FW", "10", "24"));
        m.put("Morocco", arr(
                "Yassine Bounou", "GK", "1", "33",
                "Achraf Hakimi", "DF", "2", "25",
                "Romain Saïss", "DF", "3", "34",
                "Nayef Aguerd", "DF", "4", "28",
                "Noussair Mazraoui", "DF", "5", "26",
                "Sofyan Amrabat", "MF", "6", "27",
                "Azzedine Ounahi", "MF", "8", "24",
                "Brahim Díaz", "FW", "10", "24",
                "Youssef En-Nesyri", "FW", "9", "27",
                "Hakim Ziyech", "FW", "7", "31",
                "Selim Rondić", "MF", "11", "22"));
        m.put("Japan", arr(
                "Gonda Shuichi", "GK", "1", "35",
                "Hiroki Ito", "DF", "2", "25",
                "Wataru Endo", "MF", "3", "31",
                "Ko Itakura", "DF", "4", "27",
                "Yukinari Sugawara", "DF", "5", "23",
                "Kaoru Mitoma", "FW", "7", "27",
                "Takefusa Kubo", "FW", "10", "23",
                "Ritsu Doan", "FW", "11", "26",
                "Daizen Maeda", "FW", "9", "26",
                "Junya Ito", "FW", "8", "31",
                "Wataru Endo", "MF", "6", "31"));
        m.put("South Korea", arr(
                "Kim Seung-gyu", "GK", "1", "34",
                "Kim Min-jae", "DF", "3", "27",
                "Kim Young-gwon", "DF", "4", "34",
                "Kim Jin-su", "DF", "5", "32",
                "Jeong Seung-hyun", "DF", "2", "28",
                "Lee Kang-in", "MF", "10", "23",
                "Hwang Hee-chan", "FW", "11", "28",
                "Son Heung-min", "FW", "7", "32",
                "Cho Gue-sung", "FW", "9", "26",
                "Paik Seung-ho", "MF", "8", "27",
                "Jung Woo-young", "MF", "6", "32"));
        m.put("Colombia", arr(
                "Camilo Vargas", "GK", "1", "35",
                "Santiago Arias", "DF", "2", "32",
                "Davinson Sánchez", "DF", "3", "28",
                "Yerry Mina", "DF", "4", "29",
                "Johan Mojica", "DF", "5", "31",
                "Wilmar Barrios", "MF", "6", "30",
                "James Rodríguez", "MF", "10", "33",
                "Luis Díaz", "FW", "7", "27",
                "Luis Sinisterra", "FW", "11", "25",
                "Rafael Borré", "FW", "9", "28",
                "Jhon Arias", "MF", "8", "26"));
        m.put("Croatia", arr(
                "Dominik Livaković", "GK", "1", "29",
                "Josip Juranović", "DF", "2", "28",
                "Joško Gvardiol", "DF", "3", "22",
                "Dejan Lovren", "DF", "4", "34",
                "Borna Sosa", "DF", "5", "26",
                "Marcelo Brozović", "MF", "6", "31",
                "Luka Modrić", "MF", "10", "38",
                "Mateo Kovačić", "MF", "8", "30",
                "Ivan Perišić", "FW", "7", "35",
                "Bruno Petković", "FW", "9", "30",
                "Marko Livaja", "FW", "11", "30"));
        return m;
    }

    private static String[][] arr(String... data) {
        String[][] result = new String[data.length / 4][4];
        for (int i = 0; i < data.length; i += 4) {
            result[i / 4] = new String[]{data[i], data[i + 1], data[i + 2], data[i + 3]};
        }
        return result;
    }
}
