<Form.Field error={!!errors.@name@}>
                <label htmlFor="@name@">@name@</label>
                <input 
                    type="@type@" 
                    id="@name@" 
                    name="@name@" 
                    placeholder="@name@"
                    value={data.@name@}
                    onChange={this.onChange}
            />
        </Form.Field>
        {errors.@name@ && <InlineError text={errors.@name@} />}
