import React from 'react';
import { Link } from "react-router-dom";
import { BROWSER_PATH } from "../../../constants/path";
import style from '../../../css/Layout/Simple/Header.module.css';

const Header = () => {
    return (
        <div className={style.header}>
            <Link to={BROWSER_PATH.BASE}>
                <img src={`${process.env.PUBLIC_URL}/assets/logo/logo_transparent.png`} alt="" style={{ width: '111px' }} />
            </Link>
        </div>
    );
};

export default Header;