private int getGoldPhaLeHoa(int star) {
        switch (star) {
            case 0:
                return 5000;
            case 1:
                return 100000;
            case 2:
                return 20000;
            case 3:
                return 40000;
            case 4:
                return 60000;
            case 5:
                return 9000;
            case 6:
                return 120;
            case 7:
                return 20000;
            case 8:
                return 2200;
            case 9:
                return 22000;
            case 10:
                return 22000;
            case 11:
                return 220000;
            case 12:
                return 220000;
        }
        return 0;
    }

    private float getRatioPhaLeHoa(int star) {
        switch (star) {
            case 0:
                return 100f;
            case 1:
                return 100f;
            case 2:
                return 100f;
            case 3:
                return 100f;
            case 4:
                return 100f;
            case 5:
                return 100f;
            case 6:
                return 100f;
            case 7:
                return 100f;
            case 8:
                return 100f;    
            case 9:
                return 100f;    
            case 10:
                return 100f;
            case 11:
                return 100f;
             case 12:
                return 100f;    
        }
        
        
        return 0;
    }

    private int getGemPhaLeHoa(int star) {
        switch (star) {
            case 0:
                return 10;
            case 1:
                return 20;
            case 2:
                return 30;
            case 3:
                return 40;
            case 4:
                return 50;
            case 5:
                return 60;
            case 6:
                return 70;
            case 7:
                return 80;
            case 8:
                return 50;    
            case 9:
                return 50;    
            case 10:
                return 50;
            case 11:
                return 50;
             case 12:
                return 50;      
        }
        return 0;
    }

    private int getGemEpSao(int star) {
        switch (star) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 5;
            case 3:
                return 10;
            case 4:
                return 25;
            case 5:
                return 50;
            case 6:
                return 100;
        }
        return 0;
    }

    private double getTileNangCapDo(int level) {
        switch (level) {
            case 0:
                return 80;
            case 1:
                return 50;
            case 2:
                return 20;
            case 3:
                return 10;
            case 4:
                return 7;
            case 5:
                return 5;
            case 6:
                return 1;
            case 7: // 7 sao
                return 0.3;
            case 8:
                return 5;
            case 9:
                return 1;
            case 10: // 7 sao
                return 0.3;
            case 11: // 7 sao
                return 0.3; 
            case 12: // 7 sao
                return 0.3;     
        }
        return 0;
    }

    private int getCountDaNangCapDo(int level) {
        switch (level) {
            case 0:
                return 3;
            case 1:
                return 7;
            case 2:
                return 11;
            case 3:
                return 17;
            case 4:
                return 23;
            case 5:
                return 35;
            case 6:
                return 50;
            case 7:
                return 70;
        }
        return 0;
    }

    private int getCountDaBaoVe(int level) {
        return level + 1;
    }

    private int getGoldNangCapDo(int level) {
        switch (level) {
            case 0:
                return 10000;
            case 1:
                return 70000;
            case 2:
                return 300000;
            case 3:
                return 1500000;
            case 4:
                return 7000000;
            case 5:
                return 23000000;
            case 6:
                return 100000000;
            case 7:
                return 250000000;
        }
        return 0;
    }