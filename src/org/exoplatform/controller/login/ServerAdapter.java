package org.exoplatform.controller.login;

import java.util.ArrayList;

import android.util.Log;
import org.exoplatform.R;
import org.exoplatform.model.ServerObjInfo;
import org.exoplatform.singleton.AccountSetting;
import org.exoplatform.singleton.ServerSettingHelper;
import org.exoplatform.utils.ExoConstants;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ServerAdapter extends BaseAdapter {
  private ArrayList<ServerObjInfo> serverInfoList;

  private ListView                 _listViewServer;

  private Context                  mContext;

  private int                      _intDomainIndex;

  private String                   _strDomain;

  private static final String TAG = "eXoServerAdapter";

  public ServerAdapter(Context context, ListView lv) {
    mContext = context;
    _listViewServer = lv;
    serverInfoList = ServerSettingHelper.getInstance().getServerInfoList();
    _intDomainIndex = Integer.valueOf(AccountSetting.getInstance().getDomainIndex());
  }

  // @Override
  public int getCount() {
    return serverInfoList.size();
  }

  // @Override
  public Object getItem(int pos) {
    return serverInfoList.get(pos);
  }

  // @Override
  public long getItemId(int pos) {
    return pos;
  }

  // @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final int pos = position;
    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.server_list_item, parent, false);

    final ServerObjInfo serverObj = serverInfoList.get(pos);
    TextView txtvServerName = (TextView) rowView.findViewById(R.id.TextView_ServerName);
    txtvServerName.setText(serverObj._strServerName);

    TextView txtvUrl = (TextView) rowView.findViewById(R.id.TextView_URL);
    txtvUrl.setText(serverObj._strServerUrl);

    ImageView imgView = (ImageView) rowView.findViewById(R.id.ImageView_Checked);
    if (_intDomainIndex == pos) {
      imgView.setBackgroundResource(R.drawable.authenticate_checkmark_on);
    } else {
      imgView.setBackgroundResource(R.drawable.authenticate_checkmark_off);
    }

    rowView.setOnClickListener(new OnClickListener() {

      public void onClick(View v) {
        Log.i(TAG, "onClick server list item");

        View rowView = getView(pos, null, _listViewServer);
        ImageView imgView = (ImageView) rowView.findViewById(R.id.ImageView_Checked);
        imgView.setBackgroundResource(R.drawable.authenticate_checkmark_off);

        _intDomainIndex = pos;
        _strDomain = serverObj._strServerUrl;
        AccountSetting.getInstance().setDomainIndex(String.valueOf(_intDomainIndex));
        AccountSetting.getInstance().setDomainName(_strDomain);

        AccountSetting.getInstance().setUsername(serverObj.username);
        AccountSetting.getInstance().setPassword(serverObj.password);
        // TODO: set username and password

        SharedPreferences.Editor editor = mContext.getSharedPreferences(ExoConstants.EXO_PREFERENCE, 0)
                                                  .edit();
        editor.putString(ExoConstants.EXO_PRF_DOMAIN, AccountSetting.getInstance().getDomainName());
        editor.putString(ExoConstants.EXO_PRF_DOMAIN_INDEX, AccountSetting.getInstance()
                                                                          .getDomainIndex());
        editor.putString(ExoConstants.EXO_PRF_USERNAME, serverObj.username);
        editor.putString(ExoConstants.EXO_PRF_PASSWORD, serverObj.password);
        editor.commit();

        rowView = getView(_intDomainIndex, null, _listViewServer);
        imgView = (ImageView) rowView.findViewById(R.id.ImageView_Checked);
        imgView.setBackgroundResource(R.drawable.authenticate_checkmark_on);
        notifyDataSetChanged();
      }
    });

    return (rowView);

  }
}
