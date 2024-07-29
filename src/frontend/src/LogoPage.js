import React from "react";
import { Outlet } from "react-router-dom";

class LogoPage extends React.Component {
    render() {
        return (
            <>
                <div className="logo-page">
                    <div className="logo">
                        <span className="logo-text noselect">Write</span>
                        <span className="logo-text noselect" role="img" id="poop-emoji" aria-label="smiling poop">
                        💩
                        </span>
                    </div>
                    <div className="logo-page-content">
                        <Outlet />
                    </div>
                </div>
            </>
        );
    }
}

export default LogoPage;