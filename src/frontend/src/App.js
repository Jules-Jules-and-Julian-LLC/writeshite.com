import React from "react";
import "./App.css";
import Homepage from "./Homepage";
import Lobby from "./Lobby";
import Gallery from "./Gallery";
import AboutPage from "./AboutPage";
import HelpPage from "./HelpPage";
import LogoPage from "./LogoPage";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";

export default class App extends React.Component {
    render() {
        return (
            <div id="content">
                <Router>
                    <LogoPage>
                        <Routes>
                            <Route path="/" element={<Homepage/>} />
                            <Route path="/lobby/:lobbyId" element={<Lobby/>} />
                            <Route path="/gallery/:lobbyId" element={<Gallery/>} />
                            <Route path="/about" element={<AboutPage/>} />
                            <Route path="/help" element={<HelpPage/>} />
                        </Routes>
                    </LogoPage>
                </Router>
            </div>
        );
    }
}