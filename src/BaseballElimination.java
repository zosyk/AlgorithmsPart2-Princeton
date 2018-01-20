import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class BaseballElimination {

    private FordFulkerson fordFulkerson;

    private final Map<String, Integer> teamsByName = new HashMap<>();
    private final Map<Integer, String> teamsByIndex = new HashMap<>();
    private final Map<String, List<String>> certificatesOfElimination = new HashMap<>();
    private final int[] wins;
    private final int[] loses;
    private final int[] left;
    private final int[][] games;
    private final int numberOfTeams;

    private int SOURCE;
    private int TARGET;

    private boolean[] isEliminated;

    public BaseballElimination(String filename) {
        In in = new In(filename);

        numberOfTeams = in.readInt();
        wins = new int[numberOfTeams];
        loses = new int[numberOfTeams];
        left = new int[numberOfTeams];
        games = new int[numberOfTeams][numberOfTeams];
        isEliminated = new boolean[numberOfTeams];

        for (int i = 0; i < numberOfTeams; i++) {
            String teamName = in.readString();

            teamsByName.put(teamName, i);
            teamsByIndex.put(i, teamName);

            wins[i] = in.readInt();
            loses[i] = in.readInt();
            left[i] = in.readInt();

            for (int j = 0; j < numberOfTeams; j++) {
                games[i][j] = in.readInt();
            }
        }

        trivialElimination();
        nonTrivialElimination();
    }// create a baseball division from given filename in format specified below

    private void trivialElimination() {
        for (int testTeam = 0; testTeam < numberOfTeams; testTeam++) {

            int testTeamMaxWin = wins[testTeam] + left[testTeam];
            List<String> certificates = new ArrayList<>();

            for (int team = 0; team < numberOfTeams; team++) {
                if(testTeam == team) continue;

                if(testTeamMaxWin< wins[team]) {
                    certificates.add(teamsByIndex.get(team));
                }

            }
            if(!certificates.isEmpty()) {
                isEliminated[testTeam] = true;
                this.certificatesOfElimination.put(teamsByIndex.get(testTeam), certificates);
            }
        }
    }

    private void nonTrivialElimination() {

        TARGET = numberOfTeams;

        for (int testTeam = 0; testTeam < numberOfTeams; testTeam++) {

            if(isEliminated[testTeam]) continue;

            SOURCE = testTeam;
            int currIndex = numberOfTeams + 1;
            List<FlowEdge> flowEdges = new ArrayList<>();

            for (int i = 0; i < numberOfTeams; i++) {
                if (testTeam == i) continue;

                int capacity = wins[testTeam] + left[testTeam] - wins[i];
                FlowEdge teamEdge = new FlowEdge(i, TARGET, capacity > 0 ? capacity : 0);     // connect to artificial target vertex
                flowEdges.add(teamEdge);


                for (int j = i +1; j < numberOfTeams; j++) {
                    if(j == testTeam) continue;

                    FlowEdge gameEdge = new FlowEdge(SOURCE, currIndex, games[i][j]);
                    flowEdges.add(gameEdge);


                    FlowEdge gameToTeamEdge1 = new FlowEdge(currIndex, i, Double.POSITIVE_INFINITY);
                    FlowEdge gameToTeamEdge2 = new FlowEdge(currIndex, j, Double.POSITIVE_INFINITY);

                    flowEdges.add(gameToTeamEdge1);
                    flowEdges.add(gameToTeamEdge2);

                    currIndex++;
//                    System.out.println("i "+ i + " , j  "+ j);
                }
            }

//            System.out.println("Number of Edges: " + flowEdges.size());

            FlowNetwork flowNetwork = new FlowNetwork(flowEdges.size());

            flowEdges.stream().forEach(flowNetwork::addEdge);
            fordFulkerson = new FordFulkerson(flowNetwork, SOURCE, TARGET);

            int remainingGames = 0;
            for (int i = 0; i < numberOfTeams; i++) {
                if(i == testTeam) continue;
                for (int j = i + 1; j < numberOfTeams; j++) {
                    if(j == testTeam) continue;

                    remainingGames+= games[i][j];
                }
            }

            isEliminated[testTeam] = fordFulkerson.value() < remainingGames;

            if(isEliminated[testTeam]) {

                List<String> list  = new ArrayList<>();

                for (int i = 0; i < numberOfTeams; i++) {
                    if(i == testTeam) continue;

                    if(fordFulkerson.inCut(i)) {
                        list.add(teamsByIndex.get(i));
                    }
                }

                certificatesOfElimination.put(teamsByIndex.get(testTeam), list);
            }
//            System.out.println("testTeam : " + teamsByIndex.get(testTeam)  + ", fordFulkerson value: " + fordFulkerson.value() + ", remaining games: " + remainingGames);
        }
    }

    public int numberOfTeams() {
        return numberOfTeams;

    }   // number of teamsByName

    public Iterable<String> teams() {
        return new HashSet<>(teamsByName.keySet());

    } // all teamsByName

    public int wins(String team) {
        validateTeam(team);

        return wins[teamsByName.get(team)];
    }// number of wins for given team

    private void validateTeam(String... teams) {
        if (teams == null) throw new IllegalArgumentException();

        for (String team : teams) {
            Integer teamIndex = this.teamsByName.get(team);
            if (teamIndex == null) throw new IllegalArgumentException();
        }
    }

    public int losses(String team) {
        validateTeam(team);
        return loses[teamsByName.get(team)];

    }  // number of losses for given team

    public int remaining(String team) {
        validateTeam(team);
        return left[teamsByName.get(team)];

    }  // number of remaining games for given team

    public int against(String team1, String team2) {
        validateTeam(team1, team2);
        return games[teamsByName.get(team1)][teamsByName.get(team2)];

    } // number of remaining games between team1 and team2

    public boolean isEliminated(String team) {
        validateTeam(team);

        return isEliminated[teamsByName.get(team)];
    } // is given team eliminated?

    public Iterable<String> certificateOfElimination(String team) {
       validateTeam(team);

       return certificatesOfElimination.get(team);
    } // subset R of teamsByName that eliminates given team; null if not eliminated


    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);

        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}