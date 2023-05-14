
package com.example.hazirclicker2;

        import androidx.annotation.NonNull;

        import android.app.Activity;
        import android.app.Dialog;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.view.Window;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;

public class popup_window extends Dialog implements View.OnClickListener{
    public Activity c;
    public Dialog d;
    public Button yes, no;
    public int conformation=-1;
    public int leftoverCurrency;
    public String yesorno="";
    private String upgradeType="";
    private String boostType="";
    private String cost = "";
    public TextView price;
    public String skindex="";

    HelperDB hlp;
    SQLiteDatabase db;

    public popup_window(@NonNull Context context) {
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_window);
        yes = (Button) findViewById(R.id.yes);
        no = (Button) findViewById(R.id.no);
        price = (TextView) findViewById(R.id.pricetext);
        hlp = new HelperDB(getContext());
        db=hlp.getReadableDatabase();
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(price.getText().equals("This Item Costs "+cost+" Oinkers\nAre You Sure?")){
        switch (view.getId()) {
            case R.id.yes:
                yesorno="YES";
                OnConfirmedSkins(db);
                break;
            case R.id.no:
                yesorno="NO";
                dismiss();
                break;
            default:
                break;
            }
        }
        if(price.getText().equals("This Upgrade Costs " + cost + " Oinkers\nAre You Sure?")){
            switch (view.getId()) {
                case R.id.yes:
                    yesorno="YES";
                    OnConfirmedUpgrades(db);
                    break;
                case R.id.no:
                    yesorno="NO";
                    dismiss();
                    break;
                default:
                    break;
            }
        }
        if(price.getText().equals("This Boost Costs "+cost+ " Oinkers\nAre You Sure?")){
            switch (view.getId()) {
                case R.id.yes:
                    yesorno="YES";
                    OnConfirmedBoostsBuy(db);
                    break;
                case R.id.no:
                    yesorno="NO";
                    dismiss();
                    break;
                default:
                    break;
            }
        }
        if(price.getText().equals("Are You Sure You Want To Activate This Boost?")){
            switch (view.getId()) {
                case R.id.yes:
                    yesorno="YES";
                    OnConfirmedBoostsActivation(db);
                    break;
                case R.id.no:
                    yesorno="NO";
                    dismiss();
                    break;
                default:
                    break;
            }
        }
        if(price.getText().equals("Item Owned, Would You like To Equip?")){
            switch (view.getId()) {
                case R.id.yes:
                    EquipSkin(db);
                    break;
                case R.id.no:
                    dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    private void OnConfirmedBoostsActivation(SQLiteDatabase db) {
        Log.d("HERE","I conforimed boost activation");
        int newBoosttimes = ((getTimesBought(db,boostType))-1);
        setBoughtBoosts(db,newBoosttimes,boostType);
        db.execSQL("UPDATE Boosts SET isActivated = 'YES' WHERE  boostType = '"+boostType+"'");
        Log.d("HERE","I conforimed boost activation");
        Toast.makeText(getContext(),"Boost Activated! You Can view the remaining time in The Home Page",Toast.LENGTH_LONG).show();
        dismiss();
    }

    private void OnConfirmedBoostsBuy(SQLiteDatabase db) {
        String Price = cost;
        int pricenum =Integer.parseInt(cost);
        String oink = oinkersCount(db);
        int oinknum = Integer.parseInt(oink);
        leftoverCurrency = oinknum - pricenum;
        if(leftoverCurrency < 0){
            Toast toast = Toast.makeText(getContext(),"Insufficient Funds! Transaction not completed!",Toast.LENGTH_LONG);
            toast.show();
            dismiss();

        }
        else{

            //increments the timesBought by one
            setBoughtBoosts(db,getTimesBought(db,boostType)+1,boostType);
            db.execSQL("UPDATE oinkcounter SET oinkers = " + leftoverCurrency);
            Toast.makeText(getContext(),"Transaction Complete! Boost Added Successfully",Toast.LENGTH_LONG).show();
            dismiss();
        }



    }

//    private void setBoostPrice(SQLiteDatabase db, int i,String boostType) {
//        db.execSQL("UPDATE Boosts Set price = '"+i+"' WHERE boostType = '"+boostType+"'");
//    }

    private void setBoughtBoosts(SQLiteDatabase db, int newTotal,String boostType) {

        db.execSQL("UPDATE Boosts SET timesBought = '"+newTotal+"' WHERE boostType = '"+boostType+"'");
    }

    private int getTimesBought(SQLiteDatabase db,String boostType) {
        String string = "";
        Cursor cursor = db.rawQuery("SELECT timesBought FROM Boosts WHERE boostType = '"+boostType+"'",(String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            string = cursor.getString(0);
        }
        cursor.close();
        if(string!="")
            return Integer.parseInt(string);
        else
            return 0;
    }


    private void EquipSkin(SQLiteDatabase db) {
        db.execSQL("UPDATE Skins SET isEquipped = 'NO' WHERE  isEquipped = 'YES'");
        db.execSQL("UPDATE Skins SET isEquipped = 'YES' WHERE skindex = '"+skindex+"'");
        Toast toast = Toast.makeText(getContext(),"Item Equipped!",Toast.LENGTH_LONG);
        toast.show();
        dismiss();
    }

    public void setCost(String  cost){
        this.cost = cost;
    }
    public String getCost(){
        return cost;
    }
    public void setSkindex(String skindex){
        this.skindex=skindex;
    }
    public int getLeftoverCurrency(){
        return leftoverCurrency;
    }
    public void setUpgradeType(String upgradeType){this.upgradeType = upgradeType;}
    public String getUpgradeType(){return upgradeType;}
    public void setLeftoverCurrency(int leftoverCurrency){this.leftoverCurrency=leftoverCurrency;}

    private void OnConfirmedUpgrades(SQLiteDatabase db) {
        String Price = cost;
        int pricenum =Integer.parseInt(cost);
        String oink = oinkersCount(db);
        int oinknum = Integer.parseInt(oink);
        leftoverCurrency = oinknum - pricenum;
        if(leftoverCurrency < 0){
            Toast toast = Toast.makeText(getContext(),"Insufficient Funds! Upgrade not completed!",Toast.LENGTH_LONG);
            toast.show();
            dismiss();
        }
        else{
            db.execSQL("UPDATE oinkcounter SET oinkers = " + leftoverCurrency);
            db.execSQL("UPDATE Upgrades SET timesUpgraded = "+getTimesUpgradePlusOne(db)+" WHERE upgrade = '"+getUpgradeType()+"'");
            db.execSQL("UPDATE Upgrades SET price = "+(Integer.parseInt(cost)*2)+" WHERE upgrade = '"+getUpgradeType()+"'");
            Toast toast = Toast.makeText(getContext(),"Transaction Complete! Upgrade Successful!",Toast.LENGTH_LONG);
            toast.show();
            dismiss();
        }
    }
    public void OnConfirmedSkins(SQLiteDatabase db){
        String Price = cost;
        int pricenum =Integer.parseInt(cost);
        String oink = oinkersCount(db);
        int oinknum = Integer.parseInt(oink);
        leftoverCurrency = oinknum - pricenum;
        if(leftoverCurrency < 0){
            Toast toast = Toast.makeText(getContext(),"Insufficient Funds! Transaction not completed!",Toast.LENGTH_LONG);
            toast.show();
            dismiss();

        }
        else{
            db.execSQL("UPDATE oinkcounter SET oinkers = " + leftoverCurrency);
            db.execSQL("UPDATE Skins SET isBought = 'YES' WHERE skindex = '"+skindex+"'");
            Toast toast = Toast.makeText(getContext(),"Transaction Complete! Item Unlocked!",Toast.LENGTH_LONG);
            toast.show();
            dismiss();
        }
        // initiate do u wish to equip dialog box + if not enough currency toast
    }
    public String oinkersCount(SQLiteDatabase db){
        String counter2 = "";
        Cursor cursor = db.rawQuery("SELECT oinkers FROM oinkcounter", (String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            counter2 = cursor.getString(0);
        }
        cursor.close();
        return counter2;
    }
    public int getTimesUpgradePlusOne(SQLiteDatabase db){
        String counter2 = "";
        Cursor cursor = db.rawQuery("SELECT timesUpgraded FROM Upgrades WHERE upgrade= '"+upgradeType+"'", (String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            counter2 += cursor.getString(0);
        }
        cursor.close();
        int cursorint= Integer.parseInt(counter2);
        int cursorint1= cursorint+1;
        return cursorint1;
    }

    public String getBoostType() {
        return boostType;
    }

    public void setBoostType(String boostType) {
        this.boostType = boostType;
    }
}