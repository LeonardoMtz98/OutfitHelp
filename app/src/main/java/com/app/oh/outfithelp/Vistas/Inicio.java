package com.app.oh.outfithelp.Vistas;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.ViewGroup;
import com.app.oh.outfithelp.Utilidades.ViewPagerAdapter;
import com.app.oh.outfithelp.R;

public class Inicio extends Fragment {

    private OnFragmentInteractionListener mListener;
    private View view;
    private AppBarLayout appBar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public Inicio() {
        // Required empty public constructor
    }

    public static Inicio newInstance(String param1, String param2) {
        Inicio fragment = new Inicio();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_inicio, container, false);
        View parent = (View) container.getParent();
        if (appBar == null) { createAppBar(parent); }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        return view;
    }

    public void createAppBar(View parent)
    {
        appBar = (AppBarLayout) parent.findViewById(R.id.appbar);
        tabLayout = new TabLayout(getActivity());
        appBar.addView(tabLayout);
        viewPager = (ViewPager) view.findViewById(R.id.fragmentInicio);
        datosViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        });
        tabLayout.setupWithViewPager(viewPager);
    }
    public void datosViewPager (ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(new MiArmario(), "Mi Armario" );
        adapter.addFragment(new MisPeticiones(), "Mis Peticiones");
        adapter.addFragment(new Comunidad(), "Comunidad");
        viewPager.setAdapter(adapter);
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        appBar.removeView(tabLayout);
    }
}
