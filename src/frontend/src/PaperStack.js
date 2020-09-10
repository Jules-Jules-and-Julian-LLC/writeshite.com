import React from "react";

class LogoPage extends React.Component {
    render() {
//        let boxShadowString = "";
//        for(let i=0; i<this.props.count; i++) {
//            boxShadowString += "0 1px 1px rgba(0,0,0,0.15),"
//                                10px 0 0 -1px #eee`
//        }
        let style = {
            background: "#fff",
            width: "0px",
            boxShadow: `0 1px 1px rgba(0,0,0,0.15),
                10px 0 0 -1px #eee,
                10px 0 1px 0px rgba(0,0,0,0.15),
                20px 0 0 -2px #eee,
                20px 0 1px -1px rgba(0,0,0,0.15)`,
            padding: "15px"
        };
        return (
            <div class="paper" style={style}>
                {this.props.count}
            </div>
        );
    }
}

export default LogoPage;
