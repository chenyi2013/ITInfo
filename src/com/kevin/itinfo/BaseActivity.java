package com.kevin.itinfo;

import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class BaseActivity extends FragmentActivity {

	private static final String TAG = "BaseActivity";

	protected AlertDialog mAlertDialog;
	protected AsyncTask mRunnintTask;

	protected void showShortToast(int pResId) {
		showShortToast(getString(pResId));
	}

	protected void showShortToast(String pMsg) {
		Toast.makeText(this, pMsg, Toast.LENGTH_SHORT).show();
	}

	protected void showLongToast(int pResId) {
		showLongToast(getString(pResId));
	}

	protected void showLongToast(String pMsg) {
		Toast.makeText(this, pMsg, Toast.LENGTH_LONG).show();
	}

	protected boolean hasExtra(String pExtraKey) {
		if (getIntent() != null) {
			return getIntent().hasExtra(pExtraKey);
		}
		return false;
	}

	protected void openActivity(Class<?> pClass) {
		openActivity(pClass, null);
	}

	protected void openActivity(Class<?> pClass, Bundle pBundle) {
		Intent intent = new Intent(this, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	protected void startActivity(String pAction) {
		startActivity(pAction, null);
	}

	protected void startActivity(String pAction, Bundle pBundle) {
		Intent intent = new Intent(pAction);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);

	}

	public void setAlertDialogIsClose(DialogInterface pDialog, Boolean pIsClose) {
		try {
			Field field = pDialog.getClass().getSuperclass()
					.getDeclaredField("mShow");
			field.setAccessible(true);
			field.set(pDialog, pIsClose);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	protected AlertDialog showAlertDialog(String title, String message) {
		mAlertDialog = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message).show();
		return mAlertDialog;
	}

	protected AlertDialog showAlertDialog(int pTitelResID, String pMessage,
			DialogInterface.OnClickListener pOkClickListener) {
		String title = getResources().getString(pTitelResID);
		return showAlertDialog(title, pMessage, pOkClickListener, null, null);
	}

	protected AlertDialog showAlertDialog(String pTitle, String pMessage,
			DialogInterface.OnClickListener pOkClickListener,
			DialogInterface.OnClickListener pCancelClickListener,
			DialogInterface.OnDismissListener pDismissListener) {
		mAlertDialog = new AlertDialog.Builder(this)
				.setTitle(pTitle)
				.setMessage(pMessage)
				.setPositiveButton(android.R.string.ok, pOkClickListener)
				.setNegativeButton(android.R.string.cancel,
						pCancelClickListener).show();
		if (pDismissListener != null) {
			mAlertDialog.setOnDismissListener(pDismissListener);
		}
		return mAlertDialog;
	}

	protected AlertDialog showAlertDialog(String pTitle, String pMessage,
			String pPositiveButtonLabel, String pNegativeButtonLabel,
			DialogInterface.OnClickListener pOkClickListener,
			DialogInterface.OnClickListener pCancelClickListener,
			DialogInterface.OnDismissListener pDismissListener) {
		mAlertDialog = new AlertDialog.Builder(this).setTitle(pTitle)
				.setMessage(pMessage)
				.setPositiveButton(pPositiveButtonLabel, pOkClickListener)
				.setNegativeButton(pNegativeButtonLabel, pCancelClickListener)
				.show();
		if (pDismissListener != null) {
			mAlertDialog.setOnDismissListener(pDismissListener);
		}
		return mAlertDialog;
	}

	protected ProgressDialog showProgressDialog(int pTitelResID,
			String pMessage,
			DialogInterface.OnCancelListener pCancelClickListener) {
		String title = getResources().getString(pTitelResID);
		return showProgressDialog(title, pMessage, pCancelClickListener);
	}

	protected ProgressDialog showProgressDialog(String pTitle, String pMessage,
			DialogInterface.OnCancelListener pCancelClickListener) {
		mAlertDialog = ProgressDialog.show(this, pTitle, pMessage, true, true);
		mAlertDialog.setOnCancelListener(pCancelClickListener);
		return (ProgressDialog) mAlertDialog;
	}

	protected void hideKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromInputMethod(view.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	protected void handleOutmemoryError() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				Toast.makeText(BaseActivity.this, "内存空间不足!!!",
						Toast.LENGTH_LONG).show();
			}

		});
	}

	private int network_err_count = 0;

	protected void handlerNetWorkError() {
		network_err_count++;
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (network_err_count < 3) {
					Toast.makeText(BaseActivity.this, "网速好像不怎么给力啊！",
							Toast.LENGTH_SHORT).show();
				} else if (network_err_count < 5) {
					Toast.makeText(BaseActivity.this, "网速真的不给力！",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(BaseActivity.this, "唉，今天的网络怎么这么差劲！",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

	protected void handleMalformError() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(BaseActivity.this, "数据格式错误！", Toast.LENGTH_SHORT)
						.show();

			}
		});
	}

	protected void handleFatalError() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(BaseActivity.this, "发生了一点意外，程序终止！",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		});
	}
}
