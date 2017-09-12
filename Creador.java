import javax.swing.*;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import static javax.swing.JOptionPane.YES_OPTION;

/**
 * Created by Corban on 8/11/2017.
 */
public class Creador {
    private HashMap<String, List<String>> pokemonStats;
    private HashMap<String, List<String>> pokemonBody;
    private HashMap<String, List<String[]>> pokemonMoves;
    private HashMap<String, String> spawnLocation;
    private HashMap<String, List<String>> moveDex;
    private HashSet<String> implementedMoves;

    public Creador(){
        pokemonStats = new HashMap<>();
        pokemonBody = new HashMap<>();
        pokemonMoves = new HashMap<>();
        spawnLocation = new HashMap<>();
        moveDex = new HashMap<>();
        implementedMoves = new HashSet<>();

        loadPokemon();
        loadImplementedMoves();
        loadMoves();
        loadBody();
        loadLocations();
        loadMoveDex();
    }
    /*TODO:
            Enter: Page
            Read in: Template, Pokemon, Moves
            Spit out: Formatted source code
     */

    private void loadImplementedMoves(){
        try{
            Scanner s = new Scanner(new File("Resources/ImplementedMoves.txt"));
            while(s.hasNextLine()){
                implementedMoves.add(s.nextLine().trim());
            }
            s.close();
        }catch(Exception e){e.printStackTrace();}


    }
    private void loadLocations(){
        spawnLocation.put("grass", "01");
        spawnLocation.put("fire", "02");
        spawnLocation.put("water", "03");
        spawnLocation.put("bug", "04");
        spawnLocation.put("electric", "05");
        spawnLocation.put("fairy", "06");
        spawnLocation.put("poison", "07");
        spawnLocation.put("ground", "08");
        spawnLocation.put("normal", "09");
        spawnLocation.put("ghost", "10");
        spawnLocation.put("dragon", "11");
        spawnLocation.put("rock", "12");
        spawnLocation.put("steel", "13");
        spawnLocation.put("ice", "14");
        spawnLocation.put("fighting", "15");
        spawnLocation.put("psychic", "16");
        spawnLocation.put("dark", "17");
        spawnLocation.put("flying", "18");
    }

    private void loadBody(){
        try{
            Scanner s =  new Scanner(new File("Resources/Body.txt"));
            while(s.hasNextLine()){
                String input = s.nextLine();
                ArrayList<String> split = new ArrayList(Arrays.asList(input.split("\t")));
                pokemonBody.put(split.remove(0).toLowerCase(), split);
            }
            s.close();
        }catch(Exception e){e.printStackTrace();}

    }

    private void loadMoveDex(){
        try {
            Scanner s = new Scanner(new File("Resources/MoveDex.txt"));
            while(s.hasNextLine()){
                String input = s.nextLine();
                ArrayList<String> split = new ArrayList(Arrays.asList(input.split("\t")));
                moveDex.put(split.remove(0), split);
            }
            s.close();
        }catch(Exception e){e.printStackTrace();}
    }


    private void loadMoves(){
        try{
            Scanner s = new Scanner(new File("Resources/Moves.txt"));
            String currentPokemon = "";
            ArrayList<String[]> moves = new ArrayList<>();
            while(s.hasNextLine()){

                String input = s.nextLine();

                //Checks to see if its a section divider
                if(input.matches("==.+==")){
                    //Checks to see if needs to input
                    if(currentPokemon.length() > 0) {
                        ArrayList<String[]> temp = new ArrayList<>();
                        while(!moves.isEmpty()) {
                            temp.add(moves.remove(0));
                        }
                        pokemonMoves.put(currentPokemon, temp);

                    }
                    //Changes current pokemon
                    currentPokemon = input.replaceAll("=", "").toLowerCase().trim();
                }
                else if(input.length() > 0){
                    input = input.replaceAll("Start", "L1");
                    //System.out.println(Arrays.toString(input.split(" - ")));
                    moves.add(input.split(" - "));
                }
            }
            pokemonMoves.put(currentPokemon, moves);
            s.close();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void loadPokemon(){
        try{
            Scanner s = new Scanner(new File("Resources/Pokemon.txt"));
            while(s.hasNextLine()) {
                String input = s.nextLine().toLowerCase();
                String[] split = input.split("\t");
                List<String> stats = new ArrayList<>();
                stats.add(split[0]);
                stats.add(split[2]);
                try {
                    stats.add(split[3]);
                } catch (Exception e) {}
                pokemonStats.put(split[1], stats);
            }
            s.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public boolean contains(String pokemon){
        return pokemonStats.containsKey(pokemon);
    }

    public String toString(){
        String str = pokemonStats.toString();
        str = str + "\n" + pokemonMoves.toString();
        return str;
    }

    private String loadTemplate(){
        String template = "";
        try{
            Scanner s = new Scanner(new File("Resources/Template.txt"));
            while(s.hasNextLine())
                template += s.nextLine() + "\n";

        }catch (Exception e){
            e.printStackTrace();
        }
        return template;
    }

    public String generatePage(String pokemon){
        String template = loadTemplate();

        //Get Info Stored About Pokemon

        List<String> types = pokemonStats.get(pokemon);
        //Added Dex Num
        String dexNum = types.remove(0);
        template = template.replaceAll("\\*\\*\\*DEXNUMBER\\*\\*\\*", dexNum);

        //Added Location
        String location = spawnLocation.get(types.get(0));
        template = template.replaceAll("\\*\\*\\*LOCATION NUMBER\\*\\*\\*", location);

        //Added Types
        String formattedType = "";
        for (String type: types)
            formattedType += "[[File:" + type + ".png|30px]] ";
        template = template.replaceAll("\\*\\*\\*TYPES\\*\\*\\*", formattedType);


        //Added Weight and Height
        List<String> body = pokemonBody.get(pokemon);
        template = template.replaceAll("\\*\\*\\*HEIGHT\\*\\*\\*", body.remove(0));
        template = template.replaceAll("\\*\\*\\*WEIGHT\\*\\*\\*", body.remove(0));

        //Added Images References
        String firstLetter = pokemon.substring(0,1).toUpperCase();
        String remainder   = pokemon.substring(1);
        String capitalizedPokemon = firstLetter + remainder;
        template = template.replaceAll("\\*\\*\\*POKEMON\\*\\*\\*", capitalizedPokemon);

        //Added Moves
        String formattedMoves = "";
        List<String[]> moves = pokemonMoves.get(pokemon);
        for (String[] move : moves) {
            //System.out.println(move[0]);
            if (!move[0].matches("L\\d+"))
                break;
            String level = move[0].substring(1);
            String moveName = move[1];
            String cost = implementedMoves.contains(moveName)? "1":"N/A";
            List<String> moveInfo = moveDex.get(moveName);
            if (!moveInfo.isEmpty()) {
                String type = moveInfo.remove(0);
                String category = moveInfo.remove(0);
                formattedMoves += "| style=\"text-align:center;\" |" + level + "\n" +
                        "| style=\"text-align:center;\" |" + moveName + "\n" +
                        "| style=\"text-align:center;\" |[[File:" + type + ".png|30px]]\n" +
                        "| style=\"text-align:center;\" |" + category + "\n" +
                        "| style=\"text-align:center;\" |" + cost +"\n" +
                        "|-\n";
            }
        }

        template = template.replace("***MOVES***", formattedMoves);

        //Added Stats (Needs more data)
        template = template.replaceAll("\\*\\*\\*\\d.+\\*\\*\\*", "TBD");

        //Added Desc.
        //Added Appearance
        //Added Obtaining
        template = template.replaceAll("\\*\\*\\*.+\\*\\*\\*", "");



        return template;
    }

    public void generatePage() throws IOException {
        String pokemon = "";
        while(pokemon == "" || !contains(pokemon)) {
            JFrame optionFrame = new JFrame();
            String swingInput = javax.swing.JOptionPane.showInputDialog(
                    optionFrame,
                    "Enter Pokemon/Escribir el nombre del Pokemon...",
                    "Pokemon");
            if(swingInput == null)
                System.exit(-1);
            /*System.out.println("Enter the Pokémon you wish to create a page for...");
            System.out.print("Introduce el Pokémon quieres crear una pagina para> ");
            Scanner s = new Scanner(System.in);
            pokemon = s.nextLine().toLowerCase(); */
            pokemon = swingInput.trim().toLowerCase();
            System.out.println(pokemon);
        }
        FileWriter fw = new FileWriter("Pages/" + pokemon + ".txt", false);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw, true);
        pw.print(generatePage(pokemon));
        pw.close();
        JFrame frame = new JFrame();
        JOptionPane.showMessageDialog(frame, pokemon + ".txt has been created \n" +
                pokemon + ".txt se ha creado" );
    }
    public boolean checkRepeat(){
        JFrame frame = new JFrame();
        int n = JOptionPane.showConfirmDialog(
                frame,
                "Again?\n Otra Vez?",
                "",
                JOptionPane.YES_NO_OPTION);
        return n == YES_OPTION;
    }


    public static void main(String[] args) throws IOException {
        Creador creator = new Creador();
        boolean repeat = false;
        do {
            creator.generatePage();
            repeat = creator.checkRepeat();
        }while (repeat);
        System.exit(0);
    }


}
