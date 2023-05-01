import React from 'react';
import Header from "./Header";
import NavContent from "./NavContent";
import { Outlet } from "react-router-dom";

const DefaultLayout = () => {
    return (
        <>
            <Header />
            <NavContent />
            <Outlet />
        </>
    );
};

export default DefaultLayout;