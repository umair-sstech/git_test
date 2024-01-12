package sstech.com.singleexpense.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import sstech.com.singleexpense.Adapter.AdapterAccount;
import sstech.com.singleexpense.Model.ItemAccounts;
import sstech.com.singleexpense.R;
import sstech.com.singleexpense.Uttils.Uttils;
import sstech.com.singleexpense.database.Database;
import timber.log.Timber;
import timber.log.Timber;
import timber.log.Timber;
import timber.log.Timber;
import timber.log.Timber;
public class ActivityAccount extends Activity implements PopupMenu.OnMenuItemClickListener {

    ActivityAccount obj_Account;


    ArrayList<String> list_Account;
    ArrayList<ItemAccounts> list_Accounts;


    // For Database
    SQLiteDatabase database;
    Database helper;
    ImageView iv_Back;
    ListView lv_Account;
    RelativeLayout rl_Top;

    LinearLayout llBack;
    TextView txt_Header;

    View rowView;
    int int_listPosition;


    Dialog dialogPasscode;

    Animation animation;


    AdapterAccount adapterAccount;

    Dialog dialogUpdateCategory;
 RelativeLayout rl_TopHeader;

    FloatingActionButton fab;
    AdView adView;
    AdRequest adRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        obj_Account = this;

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
        helper = new Database(obj_Account);
        database = helper.getWritableDatabase();
        rl_Top = findViewById(R.id.rl_Top);
        iv_Back = findViewById(R.id.iv_Back);
        animation = AnimationUtils.loadAnimation(obj_Account,
                R.anim.sake);


        adView = (AdView) findViewById(R.id.Ads);
        adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView = (AdView) findViewById(R.id.Ads2);
        adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        //Uttils.changeStatusbar(obj_Account);
        findById();
        getAccountData();
        setAction();
        Uttils.setThemeColor(getApplicationContext(), ActivityAccount.this, rl_Top, iv_Back, "back");

    }

    private void setAction() {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAddCategoryDialoug();
            }
        });


        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                ActivityAccount.this.overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            }
        });


        lv_Account.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                rowView = view;
                int_listPosition = position;

                PopupMenu popup = new PopupMenu(obj_Account, view);
                popup.setOnMenuItemClickListener(obj_Account);
                popup.inflate(R.menu.menu_category);
                popup.show();
            }
        });

    }

    private void getAccountData() {

        list_Account = new ArrayList<>();
        list_Accounts = new ArrayList<>();

        Cursor cursor = null;


        String select = "SELECT * FROM " + Database.TABLE_ACCOUNT;

        try {
            cursor = database.rawQuery(select, null);
            try {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                            .moveToNext()) {

                        Timber.tag("Database").e("DataAlreadyHas");


                        String str_AccountId = cursor.getString(0);
                        String str_Account = cursor.getString(1);

                        list_Account.add(str_Account);
                        list_Accounts.add(new ItemAccounts(str_AccountId, str_Account));


                        Timber.tag("Category").e(str_Account);


                    }
                    cursor.close();

                    setAdapter();
                } else {


                    Timber.tag("Database").e("NoData");


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAdapter() {

        adapterAccount = new AdapterAccount(obj_Account, list_Accounts);
        lv_Account.setAdapter(adapterAccount);

    }

    private void findById() {


        llBack = findViewById(R.id.llBack);

        txt_Header = findViewById(R.id.txt_Header);
        txt_Header.setText("Account");

        lv_Account = (ListView) findViewById(R.id.lv_Account);


        rl_TopHeader = (RelativeLayout) findViewById(R.id.rl_Top);

        fab = findViewById(R.id.fab_Add);


    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        //Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.edit:
                // do your code

                showEditCategoryDialoug(list_Accounts.get(int_listPosition).getStr_AccountName(), list_Accounts.get(int_listPosition).getStr_Id());
                return true;
            case R.id.delete:
                // do your code


                int int_Data = database.delete(helper.TABLE_ACCOUNT, "Id" + "=?", new String[]{list_Accounts.get(int_listPosition).getStr_Id()});

                if (int_Data > 0) {

                    Uttils.showToast(obj_Account, "Deleted Successfully");

                    removeListItem(rowView, int_listPosition);

                    list_Accounts.remove(int_listPosition);
                    adapterAccount.notifyDataSetInvalidated();


                } else {

                    Uttils.showToast(obj_Account, "Failed");
                }

                return true;
            case R.id.cancle:
                // do your code
                return true;

            default:
                return false;
        }
    }

    public void showEditCategoryDialoug(final String strName, final String str_Id) {


        dialogPasscode = new Dialog(obj_Account);
        dialogPasscode.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPasscode.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogPasscode.getWindow().setGravity(Gravity.CENTER);

        //   dialogAddAccount.getWindow().getAttributes().windowAnimations = R.style.animationdialog;

        dialogPasscode.getWindow().getAttributes().windowAnimations = R.style.animationName;

        dialogPasscode.setCanceledOnTouchOutside(false);


        //setting custom layout to dialog_car_variant
        dialogPasscode.setContentView(R.layout.dialoug_edit_cateogory);


        RelativeLayout rl_Top = (RelativeLayout) dialogPasscode.findViewById(R.id.rl_Top);


        final LinearLayout ll_main = (LinearLayout) dialogPasscode.findViewById(R.id.ll_Main);

        TextView txt_Header = (TextView) dialogPasscode.findViewById(R.id.txt_Header);
        ImageView iv_Back = (ImageView) dialogPasscode.findViewById(R.id.iv_Back);

        iv_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogPasscode.dismiss();
            }
        });


        txt_Header.setText("Edit Category");

        final EditText edt_Category = (EditText) dialogPasscode.findViewById(R.id.edt_Category);
        edt_Category.setText(strName);


        edt_Category.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((actionId == EditorInfo.IME_ACTION_DONE)) {

                    //   dialogPasscode.dismiss();

                    Timber.e("Done");

                    //  checkPassword();

                    if (edt_Category.getText().toString() == null || edt_Category.getText().toString().equals("")) {

                        Uttils.showToast(obj_Account, "Please  Add Category Name");
                        edt_Category.startAnimation(animation);
                    } else {

                        updateAccount(edt_Category.getText().toString(), str_Id, strName);
                    }

                    return true;

                }
                return false;
            }
        });


        ViewTreeObserver vto = ll_main.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    ll_main.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    ll_main.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int ll_width = ll_main.getMeasuredWidth();
                int ll_height = ll_main.getMeasuredHeight();

                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);


                int height = ll_height;
                int width = displaymetrics.widthPixels - 100;


                dialogPasscode.getWindow().setLayout(width, height);


            }
        });


        dialogPasscode.show();
    }

    private void updateAccount(String str_CategoryName, String str_CategoryId, String str_OldName) {


        ContentValues cv = new ContentValues();
        cv.put("account", str_CategoryName);


        long data = database.update(helper.TABLE_ACCOUNT, cv, "Id" + "=?", new String[]{str_CategoryId});


        ContentValues cvcate = new ContentValues();
        cvcate.put("mAccount", str_CategoryName);

        long datacate = database.update(helper.TABLE_DAILY_Data, cvcate, "mAccount" + "=?", new String[]{str_OldName});


        if (datacate > 0) {


            dialogPasscode.dismiss();

            Uttils.showToast(obj_Account, "Category Updated Successfully");

            getAccountData();
        } else {

            Uttils.showToast(obj_Account, "Category Not Updated Successfully");
        }
    }

    private void showAddCategoryDialoug() {


        dialogUpdateCategory = new Dialog(obj_Account);
        dialogUpdateCategory.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUpdateCategory.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogUpdateCategory.getWindow().setGravity(Gravity.BOTTOM);

        //   dialogAddAccount.getWindow().getAttributes().windowAnimations = R.style.animationdialog;

        dialogUpdateCategory.getWindow().getAttributes().windowAnimations = R.style.animationName;

        dialogUpdateCategory.setCanceledOnTouchOutside(false);


        //setting custom layout to dialog_car_variant
        dialogUpdateCategory.setContentView(R.layout.dialoug_update_cateogory);

        RelativeLayout rl_TopHeader = (RelativeLayout) dialogUpdateCategory.findViewById(R.id.rl_Top);


        iv_Back = (ImageView) dialogUpdateCategory.findViewById(R.id.iv_Back);
        final TextView txt_Header = (TextView) dialogUpdateCategory.findViewById(R.id.txt_Header);

        final LinearLayout ll_main = (LinearLayout) dialogUpdateCategory.findViewById(R.id.ll_Main);


        final EditText editText = (EditText) dialogUpdateCategory.findViewById(R.id.edt_EditCategory);


        editText.setHint("Add Account Here");
        txt_Header.setText("Account");


        iv_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogUpdateCategory.dismiss();
            }
        });
        //  editText.setText(str_name);


        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (editText.getText().toString() == null || editText.getText().toString().equals("")) {


                    Uttils.showToast(obj_Account, "Please Add Account Name");


                    // editText.setError("Please Add Category Name");
                } else {

                    addAccount(editText.getText().toString());


                }

                return false;
            }

        });

        Button btn_Save = (Button) dialogUpdateCategory.findViewById(R.id.btn_Save);
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (editText.getText().toString() == null || editText.getText().toString().equals("")) {


                    Uttils.showToast(obj_Account, "Please Add Category Name");


                    // editText.setError("Please Add Category Name");
                } else {
                    addAccount(editText.getText().toString());


                }
            }


        });


        ViewTreeObserver vto = ll_main.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    ll_main.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    ll_main.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int ll_width = ll_main.getMeasuredWidth();
                int ll_height = ll_main.getMeasuredHeight();

                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);


                int height = ll_height / 2;
                int width = displaymetrics.widthPixels;


                dialogUpdateCategory.getWindow().setLayout(width, height);


            }
        });


        dialogUpdateCategory.show();
    }

    private void addAccount(String str_Account) {

        ContentValues values = new ContentValues();

        values.put("account", str_Account);


        long sucess = database.insert(Database.TABLE_ACCOUNT,
                null, values);

        if (sucess > 0) {

            Timber.tag("Database").e("Success");

            Uttils.showToast(obj_Account, "Account Added Successfully");
            dialogUpdateCategory.dismiss();


            getAccountData();

        } else {


            Timber.tag("Database").e("Failure");

            Uttils.showToast(obj_Account, "Account Not Added Successfully");
            dialogUpdateCategory.dismiss();
        }
    }

    protected void removeListItem(View rowView, final int positon) {
        final Animation animation = AnimationUtils.loadAnimation(
                obj_Account, android.R.anim.slide_in_left);
        rowView.startAnimation(animation);
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {

            public void run() {


            }
        }, 1000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        ActivityAccount.this.overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
    }

    @Override
    protected void onDestroy() {
        if (adView!=null)
        {
            adView.destroy();
        }
        super.onDestroy();

    }
}
