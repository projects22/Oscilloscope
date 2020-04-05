/* oscilloscope
 *  by moty22.co.uk
 */ 

 unsigned char i, out[60], dh, range=0;

void setup()
{
 
    //baud rate=38400
  UCSR0A = (1<<U2X0);   //fast rate
  UBRR0H = 0;
  UBRR0L = 51;
  UCSR0B = (1<<RXEN0)|(1<<TXEN0); //rx and tx enabled

    //ADC
  DIDR0 = _BV(ADC0D);  //digital input disable
  ADCSRA = _BV(ADPS2) | _BV(ADEN); //prescaler=16 250KHz
  ADMUX = _BV(REFS0);
}

void loop()
{
   //trigger at zero crossing
    test:
    ADCSRA |=_BV(ADSC);   //start conversion
    while ( !( ADCSRA & (1<<ADIF)) ){}       // Wait for conversion to complete
    out[0]=ADCL;
    dh = ADCH;
    ADCSRA |=_BV(ADIF);   //reset interrupt
    if(out[0] > 10 || out[0] < -10) {goto test;}
    
    //set 60 samples of ADC readings
  for(i=0;i<60;++i){   
    ADCSRA |=_BV(ADSC);   //start conversion
    while ( !( ADCSRA & (1<<ADIF)) ){} // Wait for conversion to complete

    out[i]=ADCL;
    dh = ADCH;
    ADCSRA |=_BV(ADIF);   //reset interrupt
    if(range){delay(1);}

  }
      //send 60 samples
    for(i=0;i<60;++i){ 
      while ( !( UCSR0A & (1<<UDRE0)) ){} // Wait for empty transmit buffer
      UDR0 = out[i];  // send the data
   }
      delay(500);

      //check bytes received
    if( (UCSR0A & (1<<RXC0)) ) {range = UDR0 - 49;}

}

