import React from 'react';
import PropTypes from 'prop-types';

import {Form, Button} from 'semantic-ui-react';
import Validator from 'validator';
import InlineError from '../messages/InlineError';

class @name@Form extends React.Component {
    state = {
        data:@data@
             ,
        loading: false,
        errors:{}
    };

    onChange  = e =>this.setState({data:{...this.state.data,[e.target.name]: e.target.value}});

    onSubmit =() =>
    {
        const errors = this.validate(this.state.data);
        this.setState({errors});
        if (Object.keys(errors).length==0)
        {
             this.props.submit(this.state.data);          
        }
    };
    
    
    validate = (data) =>
    {
        const errors = {};
       @error@
        
        return errors;
    };
    
    
    render() {
        
        const { data, errors } = this.state;
        return (
         <Form onSubmit = {this.onSubmit}>   
        
            @FormFields@
       
            <br/><Button primary>@ButtonLabel@</Button>
        </Form>
        );
    } 
}
    
	@name@Form.propTypes = {
        submit: PropTypes.func.isRequired
    };

export default @name@Form;