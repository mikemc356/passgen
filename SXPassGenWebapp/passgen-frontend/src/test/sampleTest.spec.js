import Login from '../components/login.js';
import React from 'react';
import Enzyme, { shallow, mount, EnzymeAdapter } from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';

Enzyme.configure({adapter: new Adapter()});

describe('<Login /> with no props', () => {
  it('It should render', () => {
    const wrapper = shallow('<Login/>');
    //expect(callback).toHaveBeenCalledTimes(1);
  });
});
