import React from 'react';
import {Link } from "react-router-dom";

import @name@Form from "../forms/@name@Form";



class @name@Page extends React.Component {
    
    submit =(data) =>
    {
        console.log(data);
    };

    render() {
        return ( 
            <div>
            <h1> @PageLabel@ </h1>
            <@name@Form submit = { this.submit}/> 
            
            </div>
        );
    }
}



export default @name@Page;