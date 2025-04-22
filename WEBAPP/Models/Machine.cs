namespace CapstonePage.Models
{
    public class Machine
    {
	public int Machine_Id {get; set;}
	public string Hostname {get; set;}
	public int Online {get; set;}
        public string Parking_Lot { get; set; }
        public string Parking_Space { get; set; }
        public int Parking_Space_available { get; set; }
        public string Type { get; set; }
	public decimal? Lng {get; set;}
	public decimal? Lat {get; set;}
    }
}

