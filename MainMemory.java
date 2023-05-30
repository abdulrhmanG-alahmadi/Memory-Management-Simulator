import java.util.*;

class MainMemory {
    private int MAX;
    private Map<String, int[]> memoryMap;
    private List<int[]> holes;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter MAX memory size:");
        int MAX = sc.nextInt();
        MainMemory mainMemory = new MainMemory(MAX);

        while (true) {
            System.out.println("Enter your choice:");
            System.out.println("1. Request memory");
            System.out.println("2. Release memory");
            System.out.println("3. Compact memory");
            System.out.println("4. Report status");
            System.out.println("5. Exit");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Enter process name:");
                    String processName = sc.nextLine();
                    System.out.println("Enter memory size:");
                    int size = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Enter strategy (F/B/W):");
                    char strategy = sc.nextLine().charAt(0);
                    mainMemory.requestMemory(processName, size, strategy);
                    break;
                case 2:
                    System.out.println("Enter process name:");
                    processName = sc.nextLine();
                    mainMemory.releaseMemory(processName);
                    break;
                case 3:
                    mainMemory.compactMemory();
                    break;
                case 4:
                    mainMemory.reportStatus();
                    break;
                case 5:
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
    public MainMemory(int MAX) {
        this.MAX = MAX;
        this.memoryMap = new HashMap<>();
        this.holes = new ArrayList<>();
        this.holes.add(new int[]{0, MAX});
    }

    public void requestMemory(String processName, int size, char strategy) {
        int[] block = allocateMemory(size, strategy);
        if (block != null) {
            memoryMap.put(processName, block);
            holes.remove(block);
        } else {
            System.out.println("Error: Insufficient memory to allocate");
        }
    }

    public void releaseMemory(String processName) {
        int[] block = memoryMap.remove(processName);
        if (block != null) {
            holes.add(block);
            combineHoles();
        }
    }

    public void compactMemory() {
        int[] singleBlock = new int[2];
        singleBlock[0] = holes.get(0)[0];
        singleBlock[1] = holes.get(holes.size() - 1)[1];
        for (int i = 1; i < holes.size(); i++) {
            singleBlock[1] += holes.get(i)[1] - holes.get(i)[0] + 1;
        }
        holes.clear();
        holes.add(singleBlock);
    }

    public void reportStatus() {
        System.out.println("Allocated Memory:");
        for (Map.Entry<String, int[]> entry : memoryMap.entrySet()) {
            System.out.println("Process " + entry.getKey() + ": Addresses ["
                    + entry.getValue()[0] + ":" + entry.getValue()[1] + "]");
        }
        System.out.println("Unused Memory:");
        for (int[] hole : holes) {
            System.out.println("Addresses [" + hole[0] + ":" + hole[1] + "]");
        }
    }

    private int[] allocateMemory(int size, char strategy) {
        switch (strategy) {
            case 'F':
                return allocateFirstFit(size);
            case 'B':
                return allocateBestFit(size);
            case 'W':
                return allocateWorstFit(size);
            default:
                return null;
        }
    }

    private int[] allocateFirstFit(int size) {
        for (int[] hole : holes) {
            if (hole[1] - hole[0] + 1 >= size) {
                int[] block = new int[2];
                block[0] = hole[0];
                block[1] = hole[0] + size - 1;
                hole[0] += size;
                return block;
            }
        }
        return null;
    }

    private int[] allocateBestFit(int size) {
        int min = Integer.MAX_VALUE;
        int hole = -1;
        for (int i = 0; i < this.holes.size(); i++) {
            int[] curr = this.holes.get(i);
            if (curr[1] >= size && curr[1] - curr[0] + 1 < min) {
                min = curr[1] - curr[0] + 1;
                hole = i;
            }
        }
        if (hole == -1) return null;
        int[] res = this.holes.remove(hole);
        int start = res[0];
        int end = res[0] + size - 1;
        if (end < res[1]) this.holes.add(hole, new int[]{end + 1, res[1]});
        return new int[]{start, end};
    }
    private int[] allocateWorstFit(int size) {
        int max = Integer.MIN_VALUE;
        int hole = -1;
        for (int i = 0; i < this.holes.size(); i++) {
            int[] curr = this.holes.get(i);
            if (curr[1] >= size && curr[1] - curr[0] + 1 > max) {
                max = curr[1] - curr[0] + 1;
                hole = i;
            }
        }
        if (hole == -1) return null;
        int[] res = this.holes.remove(hole);
        int start = res[0];
        int end = res[0] + size - 1;
        if (end < res[1]) this.holes.add(hole, new int[]{end + 1, res[1]});
        return new int[]{start, end};
    }
    private void combineHoles() {
        Collections.sort(holes, (a, b) -> a[0] - b[0]);
        List<int[]> newHoles = new ArrayList<>();
        int[] currentHole = holes.get(0);
        for (int i = 1; i < holes.size(); i++) {
            int[] nextHole = holes.get(i);
            if (currentHole[1] + 1 == nextHole[0]) {
                currentHole[1] = nextHole[1];
            } else {
                newHoles.add(currentHole);
                currentHole = nextHole;
            }
        }
        newHoles.add(currentHole);
        holes = newHoles;
    }


}